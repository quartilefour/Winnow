package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.Gene;
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
public class GeneInfoIngester extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GeneInfoIngester.class);

    @Value("file:${input.directory}${input.gene-info.file}")
    private Resource[] inputResources;

    @Bean
    @Order(2)
    public Job getGeneInfoIngester() {
        return jobBuilderFactory.get("GeneInfoIngester")
                .start(stepGeneInfo())
                .build();
    }

    @Bean(name = "stepGeneInfo")
    public Step stepGeneInfo() {
        return stepBuilderFactory
                .get("stepGeneInfo")
                .<Gene, Gene>chunk(1)
                .reader(readerForGene())
                .processor(processorForGene())
                .writer(writerForGene())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(50000)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                //.retryPolicy(new NeverRetryPolicy())
                .build();
    }

    @Bean
    public ItemProcessor<Gene, Gene> processorForGene() {
        return new DBLogProcessor();
    }

    @Bean
    public ItemReader<Gene> readerForGene() {
        logger.info("Reading resource: " + inputResources.toString() + " for " + this.getClass().getName());
        MultiResourceItemReader<Gene> multiResourceItemReader = new MultiResourceItemReader<>();
        multiResourceItemReader.setResources(inputResources);
        multiResourceItemReader.setStrict(true);
        FlatFileItemReader<Gene> itemReader = new FlatFileItemReader<Gene>();
        itemReader.setLineMapper(lineMapperForGene());
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        return multiResourceItemReader;
    }

    @Bean
    public LineMapper<Gene> lineMapperForGene() {
        DefaultLineMapper<Gene> lineMapper = new DefaultLineMapper<Gene>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[]{"geneId", "symbol", "synonym", "description", "type",});
        lineTokenizer.setIncludedFields(new int[]{1, 2, 4, 8, 9});
        BeanWrapperFieldSetMapper<Gene> fieldSetMapper = new BeanWrapperFieldSetMapper<Gene>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(Gene.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<Gene> writerForGene() {
        JdbcBatchItemWriter<Gene> itemWriter = new UpsertableJdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene (gene_id, symbol,type, description, synonym) VALUES (:geneId, :symbol,:type,:description,:synonym) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Gene>());
        return itemWriter;
    }

    class DBLogProcessor implements ItemProcessor<Gene, Gene> {
        public Gene process(Gene gene) throws Exception {
           // if (logger.isDebugEnabled()) {
                logger.info("Inserting Gene : " + gene);
            //}
            return gene;
        }
    }
}
