package com.cscie599.gfn.ingestor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.*;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;

/**
 * An implementation of ResourceAwareItemReaderItemStream used as delegator to read multiple .gz/non .gz files
 * @author PulkitBhanot
 */
public class GZResourceAwareItemReaderItemStream<T> implements ResourceAwareItemReaderItemStream<T> {

    protected static final Log logger = LogFactory.getLog(GZResourceAwareItemReaderItemStream.class);

    private final ResourceAwareItemReaderItemStream<T> itemReader;
    private final boolean zippedFormat;

    public GZResourceAwareItemReaderItemStream(ResourceAwareItemReaderItemStream<T> itemReader, Boolean zippedFormat) {
        this.itemReader = itemReader;
        this.zippedFormat = zippedFormat;
    }

    @Override
    public T read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        return itemReader.read();
    }

    @Override
    public void open(ExecutionContext executionContext) throws ItemStreamException {
        itemReader.open(executionContext);
    }

    @Override
    public void update(ExecutionContext executionContext) throws ItemStreamException {
        itemReader.update(executionContext);
    }

    @Override
    public void close() throws ItemStreamException {
        itemReader.close();
    }

    @Override
    public void setResource(Resource resource) {
        logger.info("Opening resource "+resource.getFilename()+" in zipped format "+zippedFormat);
        if (zippedFormat) {
            try {
                this.itemReader.setResource(new GzipLazyResource(((FileSystemResource) resource).getPath()));
            } catch (Exception e) {
                logger.error("Error reading file, will not procede forward ", e);
            }
        } else {
            this.itemReader.setResource(resource);
        }
    }
}
