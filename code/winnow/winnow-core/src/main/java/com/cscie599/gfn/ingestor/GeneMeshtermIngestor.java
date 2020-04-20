package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.GeneMeshtermPK;
import com.cscie599.gfn.importer.geneMeshterm.GeneMeshterm;
import com.cscie599.gfn.ingestor.reader.SkipSupportedMultiResourceItemReader;
import com.cscie599.gfn.ingestor.writer.UpsertableJdbcBatchItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneMeshtermIngestor extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GenePubmedIngester.class);

    @Value("file:${input.directory}${input.gene_meshterm.file}")
    private Resource[] inputResources;

    @Value("${input.GeneMeshtermIngester.skipLines:0}")
    private int linesToSkip;

    @Bean
    @Order(6)
    public Job getGeneMeshtermIngester() {
        return jobBuilderFactory.get("GeneMeshtermIngestor")
                .start(stepGeneMeshtermInfo())
                .build();
    }

    @Bean(name = "stepGeneMeshtermInfo")
    public Step stepGeneMeshtermInfo() {
        return stepBuilderFactory
                .get("stepGeneMeshtermInfo")
                .<GeneMeshterm, GeneMeshterm>chunk(ingestionBatchSize)
                .reader(readerForGeneMeshterm())
                .processor(processorForGeneMeshterm())
                .writer(writerForGeneMeshterm())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(ingestionSkipLimit)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public ItemProcessor<GeneMeshterm, com.cscie599.gfn.entities.GeneMeshterm> processorForGeneMeshterm() {
        return new DBGeneMeshProcessor();
    }

    @Bean
    public ItemReader readerForGeneMeshterm() {
        logger.info("Reading resource: " + inputResources + " for " + this.getClass().getName() + " with linesToSkip configured with " + linesToSkip);
        SkipSupportedMultiResourceItemReader<GeneMeshterm> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<GeneMeshterm>();
        multiResourceItemReader.setResources(inputResources);
        FlatFileItemReader<GeneMeshterm> itemReader = new FlatFileItemReader<GeneMeshterm>();
        itemReader.setLineMapper(lineMapperForGeneMeshterm());
        itemReader.setLinesToSkip(1);
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        return multiResourceItemReader;
    }

    @Bean
    public LineMapper<GeneMeshterm> lineMapperForGeneMeshterm() {
        DefaultLineMapper<GeneMeshterm> lineMapper = new DefaultLineMapper<GeneMeshterm>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[]{"geneId", "meshId", "pValue", "publicationCount"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2, 3});
        BeanWrapperFieldSetMapper<GeneMeshterm> fieldSetMapper = new BeanWrapperFieldSetMapper<GeneMeshterm>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);
        fieldSetMapper.setTargetType(GeneMeshterm.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<com.cscie599.gfn.entities.GeneMeshterm> writerForGeneMeshterm() {
        JdbcBatchItemWriter<com.cscie599.gfn.entities.GeneMeshterm> itemWriter = new UpsertableJdbcBatchItemWriter<com.cscie599.gfn.entities.GeneMeshterm>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene_meshterm (gene_id, mesh_id, p_value, publication_count) VALUES (:geneMeshtermPK.geneId, :geneMeshtermPK.meshId, :pValue, :publicationCount) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<com.cscie599.gfn.entities.GeneMeshterm>());
        return itemWriter;
    }

    class DBGeneMeshProcessor implements ItemProcessor<GeneMeshterm, com.cscie599.gfn.entities.GeneMeshterm> {
        public com.cscie599.gfn.entities.GeneMeshterm process(GeneMeshterm geneMeshtermImporter) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting GeneMeshterm : " + geneMeshtermImporter);
            }

            String pval = geneMeshtermImporter.getpValue();
            GeneMeshtermPK geneMeshtermPK = new GeneMeshtermPK(geneMeshtermImporter.getGeneId(), geneMeshtermImporter.getMeshId());
            com.cscie599.gfn.entities.GeneMeshterm geneMeshterm = new com.cscie599.gfn.entities.GeneMeshterm(geneMeshtermPK);
            geneMeshterm.setPValue(Double.parseDouble(geneMeshtermImporter.getpValue()));
            geneMeshterm.setPublicationCount(Integer.parseInt(geneMeshtermImporter.getPublicationCount()));

            return geneMeshterm;
        }
    }
}
