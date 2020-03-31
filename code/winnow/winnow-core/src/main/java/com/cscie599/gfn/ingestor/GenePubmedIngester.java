package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.GenePublicationPK;
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
import org.springframework.batch.item.file.MultiResourceItemReader;
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

/**
 * @author PulkitBhanot
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GenePubmedIngester extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GenePubmedIngester.class);

    @Value("file:${input.directory}${input.gene2pubmed.file}")
    private Resource[] inputResources;

    @Value("${input.GenePubmedIngester.skipLines:0}")
    private int linesToSkip;

    @Bean
    @Order(8)
    public Job getGenePubmedIngester() {
        return jobBuilderFactory.get("GenePubmedIngester")
                .start(stepGenePubmedInfo())
                .build();
    }

    @Bean(name = "stepGenePubmedInfo")
    public Step stepGenePubmedInfo() {
        return stepBuilderFactory
                .get("stepGenePubmedInfo")
                .<GenePublicationPK, GenePublicationPK>chunk(ingestionBatchSize)
                .reader(readerForGenePubmed())
                .processor(processorForGenePubmed())
                .writer(writerForGenePubmed())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(ingestionSkipLimit)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public ItemProcessor<GenePublicationPK, GenePublicationPK> processorForGenePubmed() {
        return new DBLogProcessor();
    }

    @Bean
    public ItemReader<GenePublicationPK> readerForGenePubmed() {
        logger.info("Reading resource: " + inputResources + " for " + this.getClass().getName());
        SkipSupportedMultiResourceItemReader<GenePublicationPK> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<GenePublicationPK>();
        multiResourceItemReader.setResources(inputResources);
        FlatFileItemReader<GenePublicationPK> itemReader = new FlatFileItemReader<GenePublicationPK>();
        itemReader.setLineMapper(lineMapperForGenePubmed());
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        return multiResourceItemReader;
    }

    @Bean
    public LineMapper<GenePublicationPK> lineMapperForGenePubmed() {
        DefaultLineMapper<GenePublicationPK> lineMapper = new DefaultLineMapper<GenePublicationPK>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[]{"taxId", "geneId", "publicationId"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2});
        BeanWrapperFieldSetMapper<GenePublicationPK> fieldSetMapper = new BeanWrapperFieldSetMapper<GenePublicationPK>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(GenePublicationPK.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<GenePublicationPK> writerForGenePubmed() {
        JdbcBatchItemWriter<GenePublicationPK> itemWriter = new UpsertableJdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene_publication (gene_id, publication_id, tax_id) VALUES (:geneId, :publicationId, :taxId) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GenePublicationPK>());
        return itemWriter;
    }

    class DBLogProcessor implements ItemProcessor<GenePublicationPK, GenePublicationPK> {
        public GenePublicationPK process(GenePublicationPK gene) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting GenePublicationPK : " + gene);
            }
            return gene;
        }
    }
}
