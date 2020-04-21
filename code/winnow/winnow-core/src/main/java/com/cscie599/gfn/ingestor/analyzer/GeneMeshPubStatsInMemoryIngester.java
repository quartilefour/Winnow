package com.cscie599.gfn.ingestor.analyzer;

import com.cscie599.gfn.importer.analyzer.GeneMeshPub;
import com.cscie599.gfn.ingestor.BaseIngester;
import com.cscie599.gfn.ingestor.GZResourceAwareItemReaderItemStream;
import com.cscie599.gfn.ingestor.IngeterUtil;
import com.cscie599.gfn.ingestor.analyzer.cache.InMemoryCache;
import com.cscie599.gfn.ingestor.analyzer.cache.InMemoryMapCounterWriter;
import com.cscie599.gfn.ingestor.reader.SkipSupportedMultiResourceItemReader;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;

/**
 * @author PulkitBhanot
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneMeshPubStatsInMemoryIngester extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GeneMeshPubStatsInMemoryIngester.class);

    @Value("file:${input.directory}${input.gene-mesh-raw-stats.file}")
    private Resource[] inputResources;

    @Value("${input.GeneMeshPubStatsInMemoryIngester.skipLines:0}")
    private int linesToSkip;

    @Autowired
    InMemoryCache inMemoryCache;

    @Bean
    @Order(203)
    public Job getGeneMeshPubStatsInMemoryIngester() {
        return jobBuilderFactory.get("GeneMeshPubStatsInMemoryIngester")
                .start(stepGeneMeshPubStatsInMemory())
                .build();
    }

    @Bean(name = "stepGeneMeshPubStatsInMemory")
    public Step stepGeneMeshPubStatsInMemory() {
        return stepBuilderFactory
                .get("stepGeneMeshPubStatsInMemory")
                .<GeneMeshPub, GeneMeshPub>chunk(ingestionBatchSize)
                .reader(readerForGeneMeshPubStats())
                .processor(processorForGeneMeshPubStats())
                .writer(writerForGeneMeshPubStats())
                .faultTolerant()
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public ItemProcessor<GeneMeshPub, GeneMeshPub> processorForGeneMeshPubStats() {
        return new DBLogProcessor();
    }

    @Bean
    public ItemReader<GeneMeshPub> readerForGeneMeshPubStats() {
        logger.info("Reading resource: " + inputResources + " for " + this.getClass().getName() + " with linesToSkip configured with " + linesToSkip);
        SkipSupportedMultiResourceItemReader<GeneMeshPub> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<>();
        multiResourceItemReader.setResources(inputResources);
        multiResourceItemReader.setStrict(false);
        FlatFileItemReader<GeneMeshPub> itemReader = new FlatFileItemReader<GeneMeshPub>();
        itemReader.setLineMapper(lineMapperForGeneMeshPub());
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        return multiResourceItemReader;
    }

    @Bean
    public LineMapper<GeneMeshPub> lineMapperForGeneMeshPub() {
        DefaultLineMapper<GeneMeshPub> lineMapper = new DefaultLineMapper<GeneMeshPub>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA);
        lineTokenizer.setNames(new String[]{"publicationId", "geneId", "meshId"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2});
        BeanWrapperFieldSetMapper<GeneMeshPub> fieldSetMapper = new BeanWrapperFieldSetMapper<GeneMeshPub>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(GeneMeshPub.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public InMemoryMapCounterWriter writerForGeneMeshPubStats() {
        InMemoryMapCounterWriter itemWriter = new InMemoryMapCounterWriter(inMemoryCache.getCachedGeneMeshPubStats());
        return itemWriter;
    }

    class DBLogProcessor implements ItemProcessor<GeneMeshPub, GeneMeshPub> {
        public GeneMeshPub process(GeneMeshPub geneMeshPub) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting GeneMeshPub : " + geneMeshPub);
            }
            return geneMeshPub;
        }
    }

}
