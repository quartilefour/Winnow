package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.GeneGotermPK;
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
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.interceptor.DefaultTransactionAttribute;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.io.File;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneGoTermIngester {

    protected static final Log logger = LogFactory.getLog(GeneGoTermIngester.class);

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    DataSource dataSource;

    @Value("file:${input.directory}${input.gene2go.file}")
    private Resource inputResource;

    @Bean
    @Order(4)
    public Job getGeneGoTermIngester() {
        return jobBuilderFactory.get("GeneGoTermIngester")
                .start(stepGene2Go())
                .build();
    }

    @Bean(name = "stepGene2Go")
    public Step stepGene2Go() {
        return stepBuilderFactory
                .get("stepGene2Go")
                .<GeneGotermPK, GeneGotermPK>chunk(1)
                .reader(readerForGeneGotermPK())
                .processor(processorForGeneGotermPK())
                .writer(writerForGeneGotermPK())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(50000)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public ItemProcessor<GeneGotermPK, GeneGotermPK> processorForGeneGotermPK() {
        return new DBLogProcessor();
    }

    @Bean
    public FlatFileItemReader<GeneGotermPK> readerForGeneGotermPK() {
        logger.info("Reading resource: " + inputResource.getFilename() + " for "+this.getClass().getName());
        FlatFileItemReader<GeneGotermPK> itemReader = new FlatFileItemReader<GeneGotermPK>();
        itemReader.setLineMapper(lineMapperForGeneGotermPK());
        //itemReader.setLinesToSkip(1);
        itemReader.setResource(inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<GeneGotermPK> lineMapperForGeneGotermPK() {
        DefaultLineMapper<GeneGotermPK> lineMapper = new DefaultLineMapper<GeneGotermPK>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[]{"geneId", "goId"});
        lineTokenizer.setIncludedFields(new int[]{1, 2});
        BeanWrapperFieldSetMapper<GeneGotermPK> fieldSetMapper = new BeanWrapperFieldSetMapper<GeneGotermPK>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(GeneGotermPK.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<GeneGotermPK> writerForGeneGotermPK() {
        JdbcBatchItemWriter<GeneGotermPK> itemWriter = new UpsertableJdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene_goterm (gene_id, go_id) VALUES (:geneId, :goId) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GeneGotermPK>());
        return itemWriter;
    }

    class DBLogProcessor implements ItemProcessor<GeneGotermPK, GeneGotermPK> {
        public GeneGotermPK process(GeneGotermPK gene) throws Exception {
            if(logger.isDebugEnabled()){
                logger.debug("Inserting GeneGotermPK : " + gene);
            }
            gene.setGoId(gene.getGoId().replaceAll(":", "_"));
            return gene;
        }
    }
}
