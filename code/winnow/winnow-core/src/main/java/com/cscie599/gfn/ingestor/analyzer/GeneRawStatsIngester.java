package com.cscie599.gfn.ingestor.analyzer;

import com.cscie599.gfn.importer.analyzer.GeneRawStats;
import com.cscie599.gfn.ingestor.BaseIngester;
import com.cscie599.gfn.ingestor.GZResourceAwareItemReaderItemStream;
import com.cscie599.gfn.ingestor.IngeterUtil;
import com.cscie599.gfn.ingestor.analyzer.cache.InMemoryCache;
import com.cscie599.gfn.ingestor.analyzer.cache.InMemoryMapWriter;
import com.cscie599.gfn.ingestor.reader.SkipSupportedMultiResourceItemReader;
import com.cscie599.gfn.ingestor.writer.UpsertableJdbcBatchItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
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
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;
import java.util.List;

/**
 * @author PulkitBhanot
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneRawStatsIngester extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GeneRawStatsIngester.class);

    @Value("file:${input.directory}${input.gene-raw-stats.file}")
    private Resource[] inputResources;

    @Value("${input.GeneRawStatsIngester.skipLines:0}")
    private int linesToSkip;

    @Value("${input.StatsIngester.inMemory}")
    private boolean inMemory;

    @Autowired
    InMemoryCache inMemoryCache;

    @Bean
    @Order(202)
    public Job getGeneRawStatsIngester() {
        return jobBuilderFactory.get("GeneRawStatsIngester")
                .start(stepGeneRawStats())
                .build();
    }

    @Bean(name = "stepGeneRawStats")
    public Step stepGeneRawStats() {
        return stepBuilderFactory
                .get("stepGeneRawStats")
                .<GeneRawStats, GeneRawStats>chunk(ingestionBatchSize)
                .reader(readerForGeneRawStats())
                .processor(processorForGeneRawStats())
                .writer(new MultiOutputItemWriter(writerForGene1(), writerForGene2(),inMemory))
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(ingestionSkipLimit)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                //.retryPolicy(new NeverRetryPolicy())
                .build();
    }

    @Bean
    public ItemProcessor<GeneRawStats, GeneRawStats> processorForGeneRawStats() {
        return new DBLogProcessor();
    }

    @Bean
    public ItemReader<GeneRawStats> readerForGeneRawStats() {
        logger.info("Reading resource: " + Arrays.toString(inputResources) + " for " + this.getClass().getName() + " with linesToSkip configured with " + linesToSkip);
        SkipSupportedMultiResourceItemReader<GeneRawStats> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<>();
        multiResourceItemReader.setResources(inputResources);
        multiResourceItemReader.setStrict(false);
        FlatFileItemReader<GeneRawStats> itemReader = new FlatFileItemReader<GeneRawStats>();
        itemReader.setLineMapper(lineMapperForGeneRawStats());
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        return multiResourceItemReader;
    }

    @Bean
    public LineMapper<GeneRawStats> lineMapperForGeneRawStats() {
        DefaultLineMapper<GeneRawStats> lineMapper = new DefaultLineMapper<GeneRawStats>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA);
        lineTokenizer.setNames(new String[]{"publicationsWithoutGene", "publicationsWithGene","geneId"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2});
        BeanWrapperFieldSetMapper<GeneRawStats> fieldSetMapper = new BeanWrapperFieldSetMapper<GeneRawStats>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(GeneRawStats.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<GeneRawStats> writerForGene1() {
        JdbcBatchItemWriter<GeneRawStats> itemWriter = new UpsertableJdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("UPDATE gene SET publication_count = :publicationsWithGene WHERE gene_id = :geneId");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GeneRawStats>());
        return itemWriter;
    }

    @Bean
    public InMemoryMapWriter<GeneRawStats> writerForGene2() {
        InMemoryMapWriter<GeneRawStats> itemWriter = new InMemoryMapWriter<>(inMemoryCache.getCachedGeneStats());
        return itemWriter;
    }

    class DBLogProcessor implements ItemProcessor<GeneRawStats, GeneRawStats> {
        public GeneRawStats process(GeneRawStats gene) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting GeneRawStats : " + gene);
            }
            return gene;
        }
    }

    public class MultiOutputItemWriter implements ItemWriter<GeneRawStats> {

        private final JdbcBatchItemWriter<GeneRawStats> dbStatWriter;
        private final InMemoryMapWriter<GeneRawStats> inMemoryStatWriter;
        private final boolean useInMemory;

        public MultiOutputItemWriter(JdbcBatchItemWriter<GeneRawStats> dbStatWriter, InMemoryMapWriter<GeneRawStats> inMemoryStatWriter, boolean useInMemory) {
            this.dbStatWriter = dbStatWriter;
            this.inMemoryStatWriter = inMemoryStatWriter;
            this.useInMemory = useInMemory;
        }

        @Override
        public void write(List<? extends GeneRawStats> items) throws Exception {
            if (useInMemory) {
                inMemoryStatWriter.write(items);
            } else {
                dbStatWriter.write(items);
            }
        }
    }
}
