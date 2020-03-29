package com.cscie599.gfn.ingestor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.batch.item.json.JsonObjectReader;
import org.springframework.batch.item.support.AbstractItemCountingItemStreamItemReader;
import org.springframework.core.io.Resource;
import org.springframework.lang.Nullable;

/**
 * This is exactly similar to {@link org.springframework.batch.item.json.JsonItemReader} except that it has a default argument constuctor which we need as we are processing multiple files together.
 * @author PulkitBhanot
 */
public class CustomJsonItemReader<T> extends AbstractItemCountingItemStreamItemReader<T> implements
        ResourceAwareItemReaderItemStream<T> {

    private static final Log LOGGER = LogFactory.getLog(CustomJsonItemReader.class);

    private Resource resource;

    private JsonObjectReader<T> jsonObjectReader;

    private boolean strict = true;

    public CustomJsonItemReader() {

    }

    /**
     * Set the {@link JsonObjectReader} to use to read and map Json fragments to domain objects.
     * @param jsonObjectReader the json object reader to use
     */
    public void setJsonObjectReader(JsonObjectReader<T> jsonObjectReader) {
        this.jsonObjectReader = jsonObjectReader;
    }

    /**
     * In strict mode the reader will throw an exception on
     * {@link #open(org.springframework.batch.item.ExecutionContext)} if the
     * input resource does not exist.
     * @param strict true by default
     */
    public void setStrict(boolean strict) {
        this.strict = strict;
    }

    @Override
    public void setResource(Resource resource) {
        this.resource = resource;
    }

    @Nullable
    @Override
    protected T doRead() throws Exception {
        return jsonObjectReader.read();
    }

    @Override
    protected void doOpen() throws Exception {
        if (!this.resource.exists()) {
            if (this.strict) {
                throw new IllegalStateException("Input resource must exist (reader is in 'strict' mode)");
            }
            LOGGER.warn("Input resource does not exist " + this.resource.getDescription());
            return;
        }
        if (!this.resource.isReadable()) {
            if (this.strict) {
                throw new IllegalStateException("Input resource must be readable (reader is in 'strict' mode)");
            }
            LOGGER.warn("Input resource is not readable " + this.resource.getDescription());
            return;
        }
        this.jsonObjectReader.open(this.resource);
    }

    @Override
    protected void doClose() throws Exception {
        this.jsonObjectReader.close();
    }

}