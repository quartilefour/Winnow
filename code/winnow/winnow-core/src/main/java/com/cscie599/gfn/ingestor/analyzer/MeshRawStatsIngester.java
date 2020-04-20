package com.cscie599.gfn.ingestor.analyzer;

import com.cscie599.gfn.importer.analyzer.MeshtermRawStats;
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

import java.util.List;

/**
 * @author PulkitBhanot
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class MeshRawStatsIngester  extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(MeshRawStatsIngester.class);

    @Value("file:${input.directory}${input.mesh-raw-stats.file}")
    private Resource[] inputResources;

    @Value("${input.MeshRawStatsIngester.skipLines:0}")
    private int linesToSkip;

    @Value("${input.StatsIngester.inMemory}")
    private boolean inMemory;

    @Autowired
    InMemoryCache inMemoryCache;

    @Bean
    @Order(1)
    public Job getMeshRawStatsIngester() {
        return jobBuilderFactory.get("MeshRawStatsIngester")
                .start(stepMeshRawStats())
                .build();
    }

    @Bean(name = "MeshtermRawStats")
    public Step stepMeshRawStats() {
        return stepBuilderFactory
                .get("MeshtermRawStats")
                .<MeshtermRawStats, MeshtermRawStats>chunk(ingestionBatchSize)
                .reader(readerForMeshRawStats())
                .processor(processorForMeshRawStats())
                .writer(new MultiOutputItemWriter(writerForDB(), writerForInMemory(),inMemory))
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
    public ItemProcessor<MeshtermRawStats, MeshtermRawStats> processorForMeshRawStats() {
        return new DBLogProcessor();
    }

    @Bean
    public ItemReader<MeshtermRawStats> readerForMeshRawStats() {
        logger.info("Reading resource: " + inputResources + " for " + this.getClass().getName() + " with linesToSkip configured with " + linesToSkip);
        SkipSupportedMultiResourceItemReader<MeshtermRawStats> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<>();
        multiResourceItemReader.setResources(inputResources);
        multiResourceItemReader.setStrict(false);
        FlatFileItemReader<MeshtermRawStats> itemReader = new FlatFileItemReader<MeshtermRawStats>();
        itemReader.setLineMapper(lineMapperForMesh());
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        return multiResourceItemReader;
    }

    @Bean
    public LineMapper<MeshtermRawStats> lineMapperForMesh() {
        DefaultLineMapper<MeshtermRawStats> lineMapper = new DefaultLineMapper<MeshtermRawStats>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_COMMA);
        lineTokenizer.setNames(new String[]{"publicationsWithoutTerm", "publicationsWithTerm", "meshId"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2});
        BeanWrapperFieldSetMapper<MeshtermRawStats> fieldSetMapper = new BeanWrapperFieldSetMapper<MeshtermRawStats>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(MeshtermRawStats.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<MeshtermRawStats> writerForDB() {
        JdbcBatchItemWriter<MeshtermRawStats> itemWriter = new UpsertableJdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("UPDATE meshterm SET publication_count = :publicationsWithTerm WHERE mesh_id = :meshId");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<MeshtermRawStats>());
        return itemWriter;
    }

    @Bean
    public InMemoryMapWriter<MeshtermRawStats> writerForInMemory() {
        InMemoryMapWriter<MeshtermRawStats> itemWriter = new InMemoryMapWriter<MeshtermRawStats>(inMemoryCache.getCachedMeshStats());
        return itemWriter;
    }

    class DBLogProcessor implements ItemProcessor<MeshtermRawStats, MeshtermRawStats> {
        public MeshtermRawStats process(MeshtermRawStats mesh) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting MeshRawStats : " + mesh);
            }
            return mesh;
        }
    }

    public class MultiOutputItemWriter implements ItemWriter<MeshtermRawStats> {

        private final JdbcBatchItemWriter<MeshtermRawStats> dbStatWriter;
        private final InMemoryMapWriter<MeshtermRawStats> inMemoryStatWriter;
        private final boolean useInMemory;

        public MultiOutputItemWriter(JdbcBatchItemWriter<MeshtermRawStats> dbStatWriter, InMemoryMapWriter<MeshtermRawStats> inMemoryStatWriter, boolean useInMemory) {
            this.dbStatWriter = dbStatWriter;
            this.inMemoryStatWriter = inMemoryStatWriter;
            this.useInMemory = useInMemory;
        }

        @Override
        public void write(List<? extends MeshtermRawStats> items) throws Exception {
            if (useInMemory) {
                inMemoryStatWriter.write(items);
            } else {
                dbStatWriter.write(items);
            }
        }
    }
}
