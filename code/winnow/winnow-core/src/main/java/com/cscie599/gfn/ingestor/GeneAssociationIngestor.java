package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.GeneAssociationPK;
import com.cscie599.gfn.importer.geneAssociation.GeneAssociation;
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

import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneAssociationIngestor extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GeneAssociationIngestor.class);

    @Value("file:${input.directory}${input.gene_association.file}")
    private Resource[] inputResources;

    @Value("${input.GeneAssociationIngestor.skipLines:0}")
    private int linesToSkip;

    @Bean
    @Order(9)
    public Job getGeneAssociationIngestor() {
        return jobBuilderFactory.get("GeneAssociationIngestor")
                .start(stepGeneAssociationInfo())
                .build();
    }

    @Bean(name = "stepGeneAssociationInfo")
    public Step stepGeneAssociationInfo() {
        return stepBuilderFactory
                .get("stepGeneAssociationInfo")
                .<GeneAssociation, GeneAssociation>chunk(ingestionBatchSize)
                .reader(readerForGeneAssociation())
                .processor(processorForGeneAssociation())
                .writer(writerForGeneAssociation())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(ingestionSkipLimit)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public ItemProcessor<GeneAssociation, com.cscie599.gfn.entities.GeneAssociation> processorForGeneAssociation() {
        return new DBGeneAssociationProcessor();
    }

    @Bean
    public ItemReader readerForGeneAssociation() {
        logger.info("Reading resource: " + Arrays.toString(inputResources) + " for " + this.getClass().getName() + " with linesToSkip configured with " + linesToSkip);
        SkipSupportedMultiResourceItemReader<GeneAssociation> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<GeneAssociation>();
        multiResourceItemReader.setResources(inputResources);
        FlatFileItemReader<GeneAssociation> itemReader = new FlatFileItemReader<GeneAssociation>();
        itemReader.setLineMapper(lineMapperForGeneAssociation());
        itemReader.setLinesToSkip(1);
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        return multiResourceItemReader;
    }

    @Bean
    public LineMapper<GeneAssociation> lineMapperForGeneAssociation() {
        DefaultLineMapper<GeneAssociation> lineMapper = new DefaultLineMapper<GeneAssociation>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[]{"geneId", "otherGeneId", "pValue", "publicationCount"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2, 3});
        BeanWrapperFieldSetMapper<GeneAssociation> fieldSetMapper = new BeanWrapperFieldSetMapper<GeneAssociation>();
        fieldSetMapper.setDistanceLimit(1);
        fieldSetMapper.setTargetType(GeneAssociation.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<com.cscie599.gfn.entities.GeneAssociation> writerForGeneAssociation() {
        JdbcBatchItemWriter<com.cscie599.gfn.entities.GeneAssociation> itemWriter = new UpsertableJdbcBatchItemWriter<com.cscie599.gfn.entities.GeneAssociation>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene_association (gene_id, other_gene_id, p_value, publication_count) VALUES (:geneAssociationPK.geneId, :geneAssociationPK.otherGeneId, :pValue, :publicationCount) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<com.cscie599.gfn.entities.GeneAssociation>());
        return itemWriter;
    }

    class DBGeneAssociationProcessor implements ItemProcessor<GeneAssociation, com.cscie599.gfn.entities.GeneAssociation> {
        public com.cscie599.gfn.entities.GeneAssociation process(GeneAssociation geneAssociationImporter) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting GeneAssociation : " + geneAssociationImporter);
            }

            String pval = geneAssociationImporter.getpValue();
            GeneAssociationPK geneAssociationPK = new GeneAssociationPK(geneAssociationImporter.getGeneId(), geneAssociationImporter.getOtherGeneId());
            com.cscie599.gfn.entities.GeneAssociation geneAssociation = new com.cscie599.gfn.entities.GeneAssociation(geneAssociationPK);
            geneAssociation.setPValue(Double.parseDouble(geneAssociationImporter.getpValue()));
            geneAssociation.setPublicationCount(Integer.parseInt(geneAssociationImporter.getPublicationCount()));

            return geneAssociation;
        }
    }
}
