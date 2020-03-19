package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.GeneMeshterm;
import com.cscie599.gfn.entities.GeneMeshtermPK;
import com.cscie599.gfn.entities.GenePublicationPK;
import com.cscie599.gfn.ingestor.writer.UpsertableJdbcBatchItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneMeshtermIngestor {

    protected static final Log logger = LogFactory.getLog(GenePubmedIngester.class);

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;

    @Value("file:${input.gene_meshterm.file}")
    private Resource inputResource;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    @Order(11)
    public Job getGeneMeshtermIngester() {
        return jobBuilderFactory.get("GeneMeshtermIngester")
                .start(stepGeneMeshtermInfo())
                .build();
    }

    @Bean(name = "stepGeneMeshtermInfo")
    public Step stepGeneMeshtermInfo() {
        return stepBuilderFactory
                .get("stepGeneMeshtermInfo")
                .<GeneMeshterm, GeneMeshterm>chunk(1)
                .reader(readerForGeneMeshterm())
                .processor(processorForGeneMeshterm())
                .writer(writerForGeneMeshterm())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(50000)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public ItemProcessor<GeneMeshterm, GeneMeshterm> processorForGeneMeshterm() {
        return new DBGeneMeshProcessor();
    }

    @Bean
    public FlatFileItemReader<GeneMeshterm> readerForGeneMeshterm() {
        logger.info("Reading resource: " + inputResource.getFilename() + " for "+this.getClass().getName());

        try {
            /*InputStream stream = inputResource.getInputStream();
            int content;
            while ((content = stream.read()) != 1) {
                char c = (char) content;
            }*/
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        FlatFileItemReader<GeneMeshterm> itemReader = new FlatFileItemReader<GeneMeshterm>();
        itemReader.setLineMapper(lineMapperForGeneMeshterm());
        itemReader.setResource(inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<GeneMeshterm> lineMapperForGeneMeshterm() {
        DefaultLineMapper<GeneMeshterm> lineMapper = new DefaultLineMapper<GeneMeshterm>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[] { "gene", "meshterm", "pValue", "publicationCount"});
        lineTokenizer.setIncludedFields(new int[] { 0, 1, 2, 3 });
        BeanWrapperFieldSetMapper<GeneMeshterm> fieldSetMapper = new BeanWrapperFieldSetMapper<GeneMeshterm>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(GeneMeshterm.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<GeneMeshterm> writerForGeneMeshterm() {
        JdbcBatchItemWriter<GeneMeshterm> itemWriter = new UpsertableJdbcBatchItemWriter<GeneMeshterm>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene_meshterm (gene_id, mesh_id, p-value, publication_count) VALUES (:gene, :meshterm, :pValue, :publicationCount) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GeneMeshterm>());
        return itemWriter;
    }

    class DBGeneMeshProcessor implements ItemProcessor<GeneMeshterm, GeneMeshterm>
    {
        public GeneMeshterm process(GeneMeshterm geneMeshterm) throws Exception
        {
            if(logger.isDebugEnabled()){
                logger.debug("Inserting GeneMeshterm : " + geneMeshterm);
            }
            return geneMeshterm;
        }
    }
}
