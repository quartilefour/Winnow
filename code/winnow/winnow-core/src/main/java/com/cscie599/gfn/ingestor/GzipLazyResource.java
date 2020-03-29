package com.cscie599.gfn.ingestor;

import org.springframework.core.io.FileSystemResource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;
import org.springframework.core.io.Resource;

/**
 * This class has been referenced from https://stackoverflow.com/questions/31984393/spring-batch-process-an-encoded-zipped-file
 * @author PulkitBhanot
 */

public class GzipLazyResource extends FileSystemResource implements Resource  {

    public GzipLazyResource(File file) {
        super(file);
    }

    public GzipLazyResource(String path) {
        super(path);
    }

    @Override
    public InputStream getInputStream() throws IOException {
        return new GZIPInputStream(super.getInputStream());
    }
}