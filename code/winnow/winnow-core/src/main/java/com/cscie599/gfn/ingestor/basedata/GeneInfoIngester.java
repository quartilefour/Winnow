package com.cscie599.gfn.ingestor.basedata;

import com.cscie599.gfn.entities.Gene;
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
import org.springframework.dao.EmptyResultDataAccessException;

import java.util.Arrays;

/**
 * Ingester that ingests Gene data available from ncbi https://ftp.ncbi.nlm.nih.gov/gene/DATA/gene_info.gz.
 * <p>
 * Columns that we ingest from the dataset as
 * tax_id:
 * the unique identifier provided by NCBI Taxonomy
 * for the species or strain/isolate
 * GeneID:
 * the unique identifier for a gene
 * ASN1:  geneid
 * Symbol:
 * the default symbol for the gene
 * ASN1:  gene->locus
 * description:
 * a descriptive name for this gene
 * type of gene:
 * the type assigned to the gene according to the list of options
 * provided in https://www.ncbi.nlm.nih.gov/IEB/ToolBox/CPP_DOC/lxr/source/src/objects/entrezgene/entrezgene.asn
 *
 * @author PulkitBhanot
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GeneInfoIngester extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GeneInfoIngester.class);

    // List of files to be processed for this dataset
    @Value("file:${input.directory}${input.gene-info.file}")
    private Resource[] inputResources;

    // Lines to be skipped when ingesting the dataset. This is used when resuming from a previous checkpoint.
    @Value("${input.GeneInfoIngester.skipLines:0}")
    private int linesToSkip;

    /**
     * Returns a reference of the job to represent the ingestion of the job.
     */
    @Bean
    @Order(2)
    public Job getGeneInfoIngester() {
        return jobBuilderFactory.get("GeneInfoIngester")
                .start(stepGeneInfo())
                .build();
    }

    /**
     * Returns a Step to represent all the steps involved in the ingestion of genes.
     */
    @Bean(name = "stepGeneInfo")
    public Step stepGeneInfo() {
        return stepBuilderFactory
                .get("stepGeneInfo")
                .<Gene, Gene>chunk(ingestionBatchSize)
                .reader(readerForGene())
                .processor(processorForGene())
                .writer(writerForGene())
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(ingestionSkipLimit)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                //.retryPolicy(new NeverRetryPolicy())
                .build();
    }

    /**
     * Returns a processor for the Genes, this is used for manipulating the Gene object
     */
    @Bean
    public ItemProcessor<Gene, Gene> processorForGene() {
        return new DBLogProcessor();
    }

    /**
     * Returns a Reader for reading the gene information from the files.
     */
    @Bean
    public ItemReader<Gene> readerForGene() {
        logger.info("Reading resource: " + Arrays.toString(inputResources) + " for " + this.getClass().getName() + " with linesToSkip configured with " + linesToSkip);
        SkipSupportedMultiResourceItemReader<Gene> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<>();
        multiResourceItemReader.setResources(inputResources);
        multiResourceItemReader.setStrict(true);
        FlatFileItemReader<Gene> itemReader = new FlatFileItemReader<Gene>();
        itemReader.setLineMapper(lineMapperForGene());
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(itemReader, useZippedFormat));
        return multiResourceItemReader;
    }

    /**
     * Returns a mapper for the row to POJO Object
     */
    @Bean
    public LineMapper<Gene> lineMapperForGene() {
        DefaultLineMapper<Gene> lineMapper = new DefaultLineMapper<Gene>();
        DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer(DelimitedLineTokenizer.DELIMITER_TAB);
        lineTokenizer.setNames(new String[]{"taxId", "geneId", "symbol", "synonym", "description", "type",});
        lineTokenizer.setIncludedFields(new int[]{0, 1, 2, 4, 8, 9});
        BeanWrapperFieldSetMapper<Gene> fieldSetMapper = new BeanWrapperFieldSetMapper<Gene>();
        fieldSetMapper.setStrict(true);
        fieldSetMapper.setDistanceLimit(1);

        fieldSetMapper.setTargetType(Gene.class);
        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);
        return lineMapper;
    }

    /**
     * Returns a DB item writer and uses an UpsertableJdbcBatchItemWriter to upsert the records into DB.
     */
    @Bean
    public JdbcBatchItemWriter<Gene> writerForGene() {
        JdbcBatchItemWriter<Gene> itemWriter = new UpsertableJdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO gene (gene_id, tax_id, symbol,type, description) VALUES (:geneId, :taxId, :symbol,:type,:description) ON CONFLICT DO NOTHING RETURNING gene_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Gene>());
        return itemWriter;
    }

    /**
     * Am implementation of {@href ItemProcessor} currently it is a place holder if we want to to any manipulation on the Gene Object.
     */
    class DBLogProcessor implements ItemProcessor<Gene, Gene> {
        public Gene process(Gene gene) throws Exception {
            if (logger.isDebugEnabled()) {
                logger.debug("Inserting Gene : " + gene);
            }
            return gene;
        }
    }
}
