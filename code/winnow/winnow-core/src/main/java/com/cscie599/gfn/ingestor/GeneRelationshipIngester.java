package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.GeneGene;
import com.cscie599.gfn.entities.GeneGenePK;
import com.cscie599.gfn.entities.GeneRelationship;
import com.cscie599.gfn.importer.genegroup.GeneGroup;
import com.cscie599.gfn.ingestor.writer.UpsertableJdbcBatchItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
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

import java.util.ArrayList;
import java.util.List;

/**
 * @author PulkitBhanot
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneRelationshipIngester extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GeneRelationshipIngester.class);

    @Value("file:${input.directory}${input.gene_group.file}")
    private Resource inputResource;

    @Bean
    @Order(5)
    public Job getGeneRelationshipIngester() {
        return jobBuilderFactory.get("GeneRelationshipIngester")
                .start(stepGeneGroup())
                .build();
    }

    @Bean(name = "stepGeneGroup")
    public Step stepGeneGroup() {
        return stepBuilderFactory
                .get("stepGeneGroup")
                .<GeneGroup, List<Object>>chunk(1)
                .reader(readerForGeneGroup())
                .processor(processorForGeneGroup())
                .writer(new MultiOutputItemWriter(writerForGeneRelationship(), writerForGeneToGene()))
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(50000)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public ItemProcessor<GeneGroup, List<Object>> processorForGeneGroup() {
        return new DBLogProcessor();
    }

    @Bean
    public FlatFileItemReader<GeneGroup> readerForGeneGroup() {
        logger.info("Reading resource: " + inputResource.getFilename() + " for " + this.getClass().getName());
        FlatFileItemReader<GeneGroup> itemReader = new FlatFileItemReader<GeneGroup>();
        itemReader.setLineMapper(lineMapperForGeneGroup());
        setResource(itemReader, inputResource);
        return itemReader;
    }

    @Bean
    public LineMapper<GeneGroup> lineMapperForGeneGroup() {
        DefaultLineMapper<GeneGroup> lineMapper = new DefaultLineMapper<GeneGroup>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[]{"geneId", "relationship", "otherGeneId"});
        lineTokenizer.setIncludedFields(new int[]{1, 2, 4});
        BeanWrapperFieldSetMapper<GeneGroup> fieldSetMapper = new BeanWrapperFieldSetMapper<GeneGroup>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(GeneGroup.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<GeneRelationship> writerForGeneRelationship() {
        JdbcBatchItemWriter<GeneRelationship> itemWriter = new UpsertableJdbcBatchItemWriter<GeneRelationship>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene_relationship (relationship_id,name ) VALUES (:relationshipId, :name)  ON CONFLICT(relationship_id) DO update SET name = Excluded.name RETURNING relationship_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GeneRelationship>());
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<GeneGene> writerForGeneToGene() {
        JdbcBatchItemWriter<GeneGene> itemWriter = new UpsertableJdbcBatchItemWriter<GeneGene>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene_gene (gene_id, other_gene_id,relationship_id) VALUES (:geneGenePK.geneId, :geneGenePK.otherGeneId,:geneGenePK.relationshipId) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GeneGene>());
        return itemWriter;
    }

    class DBLogProcessor implements ItemProcessor<GeneGroup, List<Object>> {
        public List<Object> process(GeneGroup geneGroup) throws Exception {
            List<Object> returnList = new ArrayList<>();
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting GeneGroup : " + geneGroup);
                ;
            }
            GeneRelationship geneRelationship = new GeneRelationship();
            geneRelationship.setRelationshipId(geneGroup.getRelationship().toLowerCase().replaceAll("\\s+", "").trim());
            geneRelationship.setName(geneGroup.getRelationship());
            returnList.add(geneRelationship);
            GeneGenePK geneGenePK = new GeneGenePK();
            geneGenePK.setGeneId(geneGroup.getGeneId());
            geneGenePK.setOtherGeneId(geneGroup.getOtherGeneId());
            geneGenePK.setRelationshipId(geneRelationship.getRelationshipId());
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting geneGenePK : " + geneGenePK);
                ;
            }
            returnList.add(new GeneGene(geneGenePK));
            return returnList;
        }
    }

    public class MultiOutputItemWriter implements ItemWriter<Object> {

        private JdbcBatchItemWriter<GeneRelationship> delegateGeneRelationship;
        private JdbcBatchItemWriter<GeneGene> delegateGeneGene;

        public MultiOutputItemWriter(JdbcBatchItemWriter<GeneRelationship> delegateGeneRelationship, JdbcBatchItemWriter<GeneGene> delegateGeneGene) {
            this.delegateGeneRelationship = delegateGeneRelationship;
            this.delegateGeneGene = delegateGeneGene;
        }

        public void write(List<? extends Object> items) throws Exception {
            List<GeneRelationship> geneRelationships = new ArrayList<>();
            List<GeneGene> geneToGene = new ArrayList<>();

            ((List) items.get(0)).forEach(item -> {
                if (item.getClass().equals(GeneRelationship.class)) {
                    geneRelationships.add((GeneRelationship) item);
                } else if (item.getClass().equals(GeneGene.class)) {
                    geneToGene.add((GeneGene) item);
                }
            });
            delegateGeneRelationship.write(geneRelationships);
            delegateGeneGene.write(geneToGene);
        }
    }

}
