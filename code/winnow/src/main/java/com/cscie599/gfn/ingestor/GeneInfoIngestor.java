package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.Gene;
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
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneInfoIngestor {


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;

    @Value("file:${input.gene-info.file}")
    private Resource inputResource;

    @Bean(name = "stepGeneInfo")
    public Step stepGeneInfo() {
        return stepBuilderFactory
                .get("step")
                .<Gene, Gene>chunk(5)
                .reader(reader())
                .processor(processor())
                .writer(writer())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .skipLimit(50000)
                .build();
    }

    @Bean
    public ItemProcessor<Gene, Gene> processor() {
        return new DBLogProcessor();
    }

    @Bean
    public FlatFileItemReader<Gene> reader() {
        FlatFileItemReader<Gene> itemReader = new FlatFileItemReader<Gene>();
        itemReader.setLineMapper(lineMapper());
        itemReader.setLinesToSkip(1);
        itemReader.setResource(inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<Gene> lineMapper() {
        DefaultLineMapper<Gene> lineMapper = new DefaultLineMapper<Gene>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[] { "geneId", "symbol", "synonym","description","type", });
        lineTokenizer.setIncludedFields(new int[] { 1, 2,4,8,9 });
        BeanWrapperFieldSetMapper<Gene> fieldSetMapper = new BeanWrapperFieldSetMapper<Gene>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(Gene.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<Gene> writer() {
        JdbcBatchItemWriter<Gene> itemWriter = new JdbcBatchItemWriter<Gene>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene (gene_id, symbol,type, description, synonym) VALUES (:geneId, :symbol,:type,:description,:synonym) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Gene>());
        return itemWriter;
    }

    class DBLogProcessor implements ItemProcessor<Gene, Gene>
    {
        public Gene process(Gene gene) throws Exception
        {
            System.out.println("Inserting Gene : " + gene);
            return gene;
        }
    }
}
