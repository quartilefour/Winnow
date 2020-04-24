package com.cscie599.gfn.ingestor.reader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.LineCallbackHandler;
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.support.AbstractItemStreamItemReader;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

/**
 * This is a copy of {@link org.springframework.batch.item.file.MultiResourceItemReader} with added support for skipping lines across multiple files.
 *
 * @author PulkitBhanot
 */
public class SkipSupportedMultiResourceItemReader<T> extends AbstractItemStreamItemReader<T> {

    private static final Log logger = LogFactory.getLog(SkipSupportedMultiResourceItemReader.class);

    private static final String RESOURCE_KEY = "resourceIndex";

    private ResourceAwareItemReaderItemStream<? extends T> delegate;

    private Resource[] resources;

    private boolean saveState = true;

    private int currentResource = -1;

    // signals there are no resources to read -> just return null on first read
    private boolean noInput;

    private boolean strict = false;

    private int linesToSkip = 0;

    private ObjectCallbackHandler<T> skippedLinesCallback;

    /**
     * @param skippedLinesCallback will be called for each one of the initial skipped objects before any items are read.
     */
    public void setSkippedLinesCallback(ObjectCallbackHandler<T> skippedLinesCallback) {
        this.skippedLinesCallback = skippedLinesCallback;
    }

    /**
     * In strict mode the reader will throw an exception on
     * {@link #open(org.springframework.batch.item.ExecutionContext)} if there are no resources to read.
     *
     * @param strict false by default
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    private Comparator<Resource> comparator = new Comparator<Resource>() {

        /**
         * Compares resource filenames.
         */
        @Override
        public int compare(Resource r1, Resource r2) {
            return r1.getFilename().compareTo(r2.getFilename());
        }

    };

    public SkipSupportedMultiResourceItemReader() {
        this.setExecutionContextName(ClassUtils.getShortName(MultiResourceItemReader.class));
    }

    /**
     * Reads the next item, jumping to next resource if necessary.
     */
    @Nullable
    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException {

        if (noInput) {
            return null;
        }

        // If there is no resource, then this is the first item, set the current
        // resource to 0 and open the first delegate.
        if (currentResource == -1) {
            currentResource = 0;
            delegate.setResource(resources[currentResource]);
            delegate.open(new ExecutionContext());
        }

        return readNextItem();
    }

    /**
     * Use the delegate to read the next item, jump to next resource if current one is exhausted. Items are appended to
     * the buffer.
     *
     * @return next item from input
     */
    private T readNextItem() throws Exception {

        T item = readFromDelegate();

        while (item == null) {

            currentResource++;

            if (currentResource >= resources.length) {
                return null;
            }

            delegate.close();
            delegate.setResource(resources[currentResource]);
            delegate.open(new ExecutionContext());

            item = readFromDelegate();
        }

        return item;
    }

    private T readFromDelegate() throws Exception {
        T item = delegate.read();
        if (item instanceof ResourceAware) {
            ((ResourceAware) item).setResource(getCurrentResource());
        }
        return item;
    }

    /**
     * Close the {@link #setDelegate(ResourceAwareItemReaderItemStream)} reader and reset instance variable values.
     */
    @Override
    public void close() throws ItemStreamException {
        super.close();

        if (!this.noInput) {
            delegate.close();
        }

        noInput = false;
    }

    /**
     * Figure out which resource to start with in case of restart, open the delegate and restore delegate's position in
     * the resource.
     */
    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        super.open(executionContext);
        Assert.notNull(resources, "Resources must be set");

        noInput = false;
        if (resources.length == 0) {
            if (strict) {
                throw new IllegalStateException(
                        "No resources to read. Set strict=false if this is not an error condition.");
            } else {
                logger.warn("No resources to read. Set strict=true if this should be an error condition.");
                noInput = true;
                return;
            }
        }

        Arrays.sort(resources, comparator);

        if (executionContext.containsKey(getExecutionContextKey(RESOURCE_KEY))) {
            currentResource = executionContext.getInt(getExecutionContextKey(RESOURCE_KEY));

            // context could have been saved before reading anything
            if (currentResource == -1) {
                currentResource = 0;
            }

            delegate.setResource(resources[currentResource]);
            delegate.open(executionContext);
        } else {
            currentResource = -1;

            for (int i = 0; i < linesToSkip; i++) {
                try {
                    T object = read();
                    if (skippedLinesCallback != null) {
                        skippedLinesCallback.handleObject(object);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }
    }

/*
    @Override
    protected void doOpen() throws Exception {
        Assert.notNull(resource, "Input resource must be set");
        Assert.notNull(recordSeparatorPolicy, "RecordSeparatorPolicy must be set");

        noInput = true;
        if (!resource.exists()) {
            if (strict) {
                throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode): " + resource);
            }
            logger.warn("Input resource does not exist " + resource.getDescription());
            return;
        }

        if (!resource.isReadable()) {
            if (strict) {
                throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode): "
                        + resource);
            }
            logger.warn("Input resource is not readable " + resource.getDescription());
            return;
        }

        reader = bufferedReaderFactory.create(resource, encoding);
        for (int i = 0; i < linesToSkip; i++) {
            String line = readLine();
            if (skippedLinesCallback != null) {
                skippedLinesCallback.handleLine(line);
            }
        }
        noInput = false;
    }
   */

    /**
     * Store the current resource index and position in the resource.
     */
    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        super.update(executionContext);
        if (saveState) {
            executionContext.putInt(getExecutionContextKey(RESOURCE_KEY), currentResource);
            delegate.update(executionContext);
        }
    }

    /**
     * @param delegate reads items from single {@link Resource}.
     */
    public void setDelegate(ResourceAwareItemReaderItemStream<? extends T> delegate) {
        this.delegate = delegate;
    }

    /**
     * Set the boolean indicating whether or not state should be saved in the provided {@link ExecutionContext} during
     * the {@link ItemStream} call to update.
     *
     * @param saveState true to update ExecutionContext. False do not update
     *                  ExecutionContext.
     */
    public void setSaveState(boolean saveState) {
        this.saveState = saveState;
    }

    /**
     * @param comparator used to order the injected resources, by default compares {@link Resource#getFilename()}
     *                   values.
     */
    public void setComparator(Comparator<Resource> comparator) {
        this.comparator = comparator;
    }

    /**
     * @param resources input resources
     */
    public void setResources(Resource[] resources) {
        Assert.notNull(resources, "The resources must not be null");
        List<Resource> resourceList = new ArrayList<>();
        if (resources.length > 0) {
            if (resources[0] instanceof FileSystemResource) {
                for (int i = 0; i < resources.length; i++) {
                    if (resources[i].getFilename() != null && !resources[i].getFilename().equalsIgnoreCase(".DS_Store") && !resources[i].getFilename().equalsIgnoreCase("_SUCCESS")) {
                        resourceList.add(resources[i]);
                    }
                }
                this.resources = resourceList.toArray(new Resource[resourceList.size()]);
                return;
            }
        }
        this.resources = Arrays.asList(resources).toArray(new Resource[resources.length]);
    }

    /**
     * Getter for the current resource.
     *
     * @return the current resource or {@code null} if all resources have been
     * processed or the first resource has not been assigned yet.
     */
    @Nullable
    public Resource getCurrentResource() {
        if (currentResource >= resources.length || currentResource < 0) {
            return null;
        }
        return resources[currentResource];
    }

    /**
     * Public setter for the number of lines to skip at the start of a file. Can be used if the file contains a header
     * without useful (column name) information, and without a comment delimiter at the beginning of the lines.
     *
     * @param linesToSkip the number of lines to skip
     */
    public void setLinesToSkip(int linesToSkip) {
        this.linesToSkip = linesToSkip;
    }
}
