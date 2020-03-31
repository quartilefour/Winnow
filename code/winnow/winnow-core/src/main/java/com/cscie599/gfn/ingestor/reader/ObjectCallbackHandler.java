package com.cscie599.gfn.ingestor.reader;

/**
 * This is a copy of {@link org.springframework.batch.item.file.MultiResourceItemReader} with added support for skipping lines across multiple files.
 * @author PulkitBhanot
 */
public interface ObjectCallbackHandler<T> {

    void handleObject(T Object);
}
