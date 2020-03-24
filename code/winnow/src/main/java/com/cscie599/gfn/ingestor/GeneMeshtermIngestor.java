package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.*;
import com.cscie599.gfn.importer.geneMeshterm.GeneMeshtermImporter;
import com.cscie599.gfn.ingestor.writer.UpsertableJdbcBatchItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
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

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneMeshtermIngestor {

    protected static final Log logger = LogFactory.getLog(GenePubmedIngester.class);

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;

    @Value("file:${input.directory}${input.gene_meshterm.file}")
    private Resource inputResource;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    @Order(6)
    public Job getGeneMeshtermIngester() {
        return jobBuilderFactory.get("GeneMeshtermIngester")
                .start(stepGeneMeshtermInfo())
                .build();
    }

    @Bean(name = "stepGeneMeshtermInfo")
    public Step stepGeneMeshtermInfo() {
        return stepBuilderFactory
                .get("stepGeneMeshtermInfo")
                .<GeneMeshtermImporter, GeneMeshtermImporter>chunk(1)
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
    public ItemProcessor<GeneMeshtermImporter, GeneMeshterm> processorForGeneMeshterm() {
        return new DBGeneMeshProcessor();
    }

    @Bean
    public ItemReader readerForGeneMeshterm() {
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
        FlatFileItemReader<GeneMeshtermImporter> itemReader = new FlatFileItemReader<GeneMeshtermImporter>();
        itemReader.setLineMapper(lineMapperForGeneMeshterm());
        itemReader.setLinesToSkip(1);
        itemReader.setResource(inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<GeneMeshtermImporter> lineMapperForGeneMeshterm(){
        DefaultLineMapper<GeneMeshtermImporter> lineMapper = new DefaultLineMapper<GeneMeshtermImporter>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[] { "geneId", "meshId", "pValue", "publicationCount"});
        lineTokenizer.setIncludedFields(new int[] { 0, 1, 2, 3 });
        BeanWrapperFieldSetMapper<GeneMeshtermImporter> fieldSetMapper = new BeanWrapperFieldSetMapper<GeneMeshtermImporter>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);
        fieldSetMapper.setTargetType(GeneMeshtermImporter.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<GeneMeshterm> writerForGeneMeshterm() {
        JdbcBatchItemWriter<GeneMeshterm> itemWriter = new UpsertableJdbcBatchItemWriter<GeneMeshterm>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene_meshterm (gene_id, mesh_id, p_value, publication_count) VALUES (:geneMeshtermPK.geneId, :geneMeshtermPK.meshId, :pValue, :publicationCount) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GeneMeshterm>());
        return itemWriter;
    }

    class DBGeneMeshProcessor implements ItemProcessor<GeneMeshtermImporter, GeneMeshterm>
    {
        public GeneMeshterm process(GeneMeshtermImporter geneMeshtermImporter) throws Exception
        {
            if(logger.isDebugEnabled()){
                logger.debug("Inserting GeneMeshterm : " + geneMeshtermImporter);
            }

            String pval = geneMeshtermImporter.getpValue();
            GeneMeshtermPK geneMeshtermPK= new GeneMeshtermPK(geneMeshtermImporter.getGeneId(), geneMeshtermImporter.getMeshId());
            GeneMeshterm geneMeshterm = new GeneMeshterm(geneMeshtermPK);
            geneMeshterm.setPValue(Double.parseDouble(geneMeshtermImporter.getpValue()));
            geneMeshterm.setPublicationCount(Integer.parseInt(geneMeshtermImporter.getPublicationCount()));

            return geneMeshterm;
        }
    }
}
