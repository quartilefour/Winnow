package com.cscie599.gfn.ingestor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.file.ResourceAwareItemReaderItemStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.web.context.support.ServletContextResource;

import javax.sql.DataSource;

/**
 * @author PulkitBhanot
 */
public abstract class BaseIngester {

    protected static final Log logger = LogFactory.getLog(MeshtermIngestor.class);

    @Autowired
    protected StepBuilderFactory stepBuilderFactory;

    @Autowired
    protected DataSource dataSource;

    @Value("${input.zippedFormat}")
    protected Boolean useZippedFormat;

    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

    <T extends ResourceAwareItemReaderItemStream> void setResource(T reader, Resource inputResource) {
        if (useZippedFormat) {
            try {
                reader.setResource(new GzipLazyResource(((ServletContextResource) inputResource).getPath()));
            } catch (Exception e) {
                logger.error("Error reading file, will not procede forward ", e);
            }
        } else {
            reader.setResource(inputResource);
        }
    }
}
