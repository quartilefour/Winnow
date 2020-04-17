package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.GeneAssociationPK;
import com.cscie599.gfn.importer.geneAssociation.GeneAssociation;
import com.cscie599.gfn.importer.meshtermCategory.MeshtermCategory;
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
public class MeshtermCategoryIngestor extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GeneAssociationIngestor.class);

    @Value("file:${input.directory}${input.meshterm_category.file}")
    private Resource[] inputResources;

    @Value("${input.MeshtermCategoryIngestor.skipLines:0}")
    private int linesToSkip;

    @Bean
    @Order(10)
    public Job getMeshtermCategoryIngestor() {
        return jobBuilderFactory.get("MeshtermCategoryIngestor")
                .start(stepMeshtermCategoryInfo())
                .build();
    }

    @Bean(name = "stepMeshtermCategoryInfo")
    public Step stepMeshtermCategoryInfo() {
        return stepBuilderFactory
                .get("stepMeshtermCategoryInfo")
                .<MeshtermCategory, MeshtermCategory>chunk(ingestionBatchSize)
                .reader(readerForMeshtermCategory())
                .processor(processorForMeshtermCategory())
                .writer(writerForMeshtermCategory())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(ingestionSkipLimit)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public ItemProcessor<MeshtermCategory, com.cscie599.gfn.entities.MeshtermCategory> processorForMeshtermCategory() {
        return new DBMeshtermCategoryProcessor();
    }

    @Bean
    public ItemReader readerForMeshtermCategory() {
        logger.info("Reading resource: " + inputResources + " for " + this.getClass().getName() + " with linesToSkip configured with " + linesToSkip);
        SkipSupportedMultiResourceItemReader<MeshtermCategory> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<MeshtermCategory>();
        multiResourceItemReader.setResources(inputResources);
        FlatFileItemReader<MeshtermCategory> itemReader = new FlatFileItemReader<MeshtermCategory>();
        itemReader.setLineMapper(lineMapperForMeshtermCategory());
        itemReader.setLinesToSkip(0);
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        return multiResourceItemReader;
    }

    @Bean
    public LineMapper<MeshtermCategory> lineMapperForMeshtermCategory() {
        DefaultLineMapper<MeshtermCategory> lineMapper = new DefaultLineMapper<MeshtermCategory>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[]{"categoryId", "name"});
        lineTokenizer.setIncludedFields(new int[]{0, 1});
        BeanWrapperFieldSetMapper<MeshtermCategory> fieldSetMapper = new BeanWrapperFieldSetMapper<MeshtermCategory>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);
        fieldSetMapper.setTargetType(MeshtermCategory.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    @Bean
    public JdbcBatchItemWriter<com.cscie599.gfn.entities.MeshtermCategory> writerForMeshtermCategory() {
        JdbcBatchItemWriter<com.cscie599.gfn.entities.MeshtermCategory> itemWriter = new UpsertableJdbcBatchItemWriter<com.cscie599.gfn.entities.MeshtermCategory>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO meshterm_category (category_id, name) VALUES (:categoryId, :name) ON CONFLICT DO NOTHING RETURNING category_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<com.cscie599.gfn.entities.MeshtermCategory>());
        return itemWriter;
    }

    class DBMeshtermCategoryProcessor implements ItemProcessor<MeshtermCategory, com.cscie599.gfn.entities.MeshtermCategory> {
        public com.cscie599.gfn.entities.MeshtermCategory process(MeshtermCategory meshtermCategoryImporter) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting MeshtermCategory : " + meshtermCategoryImporter);
            }


            com.cscie599.gfn.entities.MeshtermCategory meshtermCategory = new com.cscie599.gfn.entities.MeshtermCategory();
            meshtermCategory.setCategoryId(meshtermCategoryImporter.getCategoryId());
            meshtermCategory.setName(meshtermCategoryImporter.getName());

            return meshtermCategory;
        }
    }
}
