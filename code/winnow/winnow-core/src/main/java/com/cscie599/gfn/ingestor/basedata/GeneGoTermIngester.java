package com.cscie599.gfn.ingestor.basedata;

import com.cscie599.gfn.entities.GeneGotermPK;
import com.cscie599.gfn.ingestor.BaseIngester;
import com.cscie599.gfn.ingestor.GZResourceAwareItemReaderItemStream;
import com.cscie599.gfn.ingestor.IngeterUtil;
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
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneGoTermIngester extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GeneGoTermIngester.class);

    @Value("file:${input.directory}${input.gene2go.file}")
    private Resource[] inputResources;

    @Value("${input.GeneGoTermIngester.skipLines:0}")
    private int linesToSkip;

    @Bean
    @Order(4)
    public Job getGeneGoTermIngester() {
        return jobBuilderFactory.get("GeneGoTermIngester")
                .start(stepGene2Go())
                .build();
    }

    /**
     * For this ingestion we are going to skip all the records for which we do not have gen_ontology format available as JSON.
     * Example Record
     * 559292	856933	GO:0006355	IEA	-	regulation of transcription, DNA-templated	-	Process
     * As we only have limited number of GO available as JSON, we are going to skip creating the relations for which we
     * do not have Gene ontology definition.
     * @return
     */
    @Bean(name = "stepGene2Go")
    public Step stepGene2Go() {
        return stepBuilderFactory
                .get("stepGene2Go")
                .<GeneGotermPK, GeneGotermPK>chunk(ingestionBatchSize)
                .reader(readerForGeneGotermPK())
                .processor(processorForGeneGotermPK())
                .writer(writerForGeneGotermPK())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .skip(DataIntegrityViolationException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRetry(DataIntegrityViolationException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .noRollback(DataIntegrityViolationException.class)
                .skipLimit(ingestionSkipLimit)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public ItemProcessor<GeneGotermPK, GeneGotermPK> processorForGeneGotermPK() {
        return new DBLogProcessor();
    }

    @Bean
    public ItemReader<GeneGotermPK> readerForGeneGotermPK() {
        logger.info("Reading resource: " + Arrays.toString(inputResources) + " for " + this.getClass().getName() + " with linesToSkip configured with " + linesToSkip);
        SkipSupportedMultiResourceItemReader<GeneGotermPK> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<GeneGotermPK>();
        multiResourceItemReader.setResources(inputResources);
        multiResourceItemReader.setStrict(true);
        FlatFileItemReader<GeneGotermPK> itemReader = new FlatFileItemReader<GeneGotermPK>();
        itemReader.setLineMapper(lineMapperForGeneGotermPK());
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        return multiResourceItemReader;
    }

    @Bean
    public LineMapper<GeneGotermPK> lineMapperForGeneGotermPK() {
        DefaultLineMapper<GeneGotermPK> lineMapper = new DefaultLineMapper<GeneGotermPK>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[]{"taxId", "geneId", "goId"});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2});
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
        itemWriter.setSql("INSERT INTO gene_goterm (gene_id, go_id, tax_id) VALUES (:geneId, :goId, :taxId) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<GeneGotermPK>());
        return itemWriter;
    }

    class DBLogProcessor implements ItemProcessor<GeneGotermPK, GeneGotermPK> {
        public GeneGotermPK process(GeneGotermPK gene) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting GeneGotermPK : " + gene);
            }
            gene.setGoId(gene.getGoId().replaceAll(":", "_").trim());
            return gene;
        }
    }
}
