package com.cscie599.gfn.ingestor;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;

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

    @Value("${ingestion.batch.size}")
    protected int ingestionBatchSize;

    @Value("${ingestion.skip.limit}")
    protected int ingestionSkipLimit;

    @Autowired
    protected JobBuilderFactory jobBuilderFactory;

}
