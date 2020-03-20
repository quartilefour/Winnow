package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.Meshterm;
import com.cscie599.gfn.entities.MeshtermTree;
import com.cscie599.gfn.entities.MeshtermTreePK;
import com.cscie599.gfn.importer.meshterm.DescriptorRecord;
import com.cscie599.gfn.importer.meshterm.MeshConverter;
import com.cscie599.gfn.ingestor.writer.UpsertableJdbcBatchItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.oxm.xstream.XStreamMarshaller;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;

import javax.sql.DataSource;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class MeshtermIngestor {

    protected static final Log logger = LogFactory.getLog(MeshtermIngestor.class);

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;

    // Lila: I changed this file for testing pubmed-mesh ingestion
    @Value("file:${input.new_meshsub.file}")
    private Resource inputResource;


    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Bean
    @Order(1)
    public Job getMeshtermXMLIngestor() {
        return jobBuilderFactory.get("MeshtermXMLIngestor")
                .start(stepMeshterm())
                .build();
    }

    @Bean(name = "stepMeshterm")
    public Step stepMeshterm() {
        return stepBuilderFactory
                .get("stepMeshterm")
                .<DescriptorRecord, List<Object>>chunk(1)
                .reader(readerForMeshterm())
                .processor(processorForMesh())
                .writer(new MultiOutputItemWriter(writerForMeshterm(), writerForMeshtermTree()))
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(50000)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    @Bean
    public JdbcBatchItemWriter<Meshterm> writerForMeshterm() {
        JdbcBatchItemWriter<Meshterm> itemWriter = new UpsertableJdbcBatchItemWriter<Meshterm>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO meshterm (mesh_id, date_created, date_revised, note, name) VALUES (:meshId, :dateCreated, :dateRevised, :note, :name) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Meshterm>());
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<MeshtermTree> writerForMeshtermTree() {
        JdbcBatchItemWriter<MeshtermTree> itemWriter = new UpsertableJdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO meshterm_tree (mesh_id, tree_id) VALUES (:meshtermTreePK.meshId, :meshtermTreePK.treeId) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<MeshtermTree>());
        return itemWriter;
    }

    private ItemProcessor<DescriptorRecord, List<Object>> processorForMesh() {
        return new DBMeshProcessor();
    }

    public class MultiOutputItemWriter implements ItemWriter<Object> {

        private JdbcBatchItemWriter<Meshterm> delegateMeshterm;
        private JdbcBatchItemWriter<MeshtermTree> delegateMeshtermTree;


        public MultiOutputItemWriter(JdbcBatchItemWriter<Meshterm> delegateMeshterm, JdbcBatchItemWriter<MeshtermTree> delegateMeshtermTree) {
            this.delegateMeshterm = delegateMeshterm;
            this.delegateMeshtermTree = delegateMeshtermTree;
        }

        @Transactional(isolation = Isolation.SERIALIZABLE)
        public void write(List<? extends Object> items) throws Exception {
            List<Meshterm> meshterms = new ArrayList<>();
            List<MeshtermTree> meshtermTrees = new ArrayList<>();

            ((List) items.get(0)).forEach(item -> {
                if (item.getClass().equals(Meshterm.class)) {
                    meshterms.add((Meshterm) item);
                } else if (item.getClass().equals(MeshtermTree.class)) {
                    meshtermTrees.add((MeshtermTree) item);
                }
            });
            delegateMeshterm.write(meshterms);
            delegateMeshtermTree.write(meshtermTrees);
        }
    }

    @Bean
    public StaxEventItemReader<DescriptorRecord> readerForMeshterm() {
        logger.info("Reading resource: " + inputResource.getFilename() + " for "+this.getClass().getName());
        StaxEventItemReader<DescriptorRecord> reader = new StaxEventItemReader<DescriptorRecord>();
        reader.setResource(inputResource);
        reader.setFragmentRootElementName("DescriptorRecord");
        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.getXStream().ignoreUnknownElements();
        xStreamMarshaller.getXStream().alias("DescriptorRecord", DescriptorRecord.class);
        xStreamMarshaller.getXStream().alias("DateCreated", DescriptorRecord.DateCreated.class);
        xStreamMarshaller.getXStream().alias("DateRevised", DescriptorRecord.DateRevised.class);
        xStreamMarshaller.getXStream().alias("TreeNumberList", DescriptorRecord.TreeNumberList.class);
        xStreamMarshaller.getXStream().registerConverter(new MeshConverter());
        reader.setUnmarshaller(xStreamMarshaller);
        return reader;
    }

    /**
     * Determine if there are treeNames associated with the meshterm and if so,
     * create a MeshtermTreePK and MeshtermTree.
     */
    class DBMeshProcessor implements ItemProcessor<DescriptorRecord, List<Object>> {
        public List<Object> process(DescriptorRecord descriptorRecord) throws Exception {
            List<Object> returnList = new ArrayList<>();
            DescriptorRecord record = ((DescriptorRecord) descriptorRecord);
            logger.info("the current meshterm is " + descriptorRecord.getDescriptorUI());
            if (record.getDescriptorUI() != null && record.getDescriptorName() != null && record.getDateCreated() != null
                    && record.getDateRevised() != null) {
                Meshterm meshterm = new Meshterm();

                // create new Meshterm with mesh_id == DescriptorUI
                meshterm.setMeshId(record.getDescriptorUI());

                // set the date_created
                DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                Date dateCreated = format.parse(record.getDateCreated().getYear() + "-" + record.getDateCreated().getMonth()+ "-" + record.getDateCreated().getDay());
                meshterm.setDateCreated(dateCreated);

                // set the date_revised
                DateFormat format2 = new SimpleDateFormat("yyyy-MM-dd");
                Date dateRevised = format2.parse(record.getDateRevised().getYear() + "-" + record.getDateRevised().getMonth()+ "-" + record.getDateRevised().getDay());
                meshterm.setDateRevised(dateRevised);

                // set note
                meshterm.setNote(record.getPublicMeSHNote());

                // set name
                meshterm.setName(record.getDescriptorName().getString());

                // add the meshterm to the return list
                returnList.add(meshterm);

                if (record.getDescriptorUI() != null && record.getTreeNumberList().getTreeNumbers() != null) {
                    record.getTreeNumberList().getTreeNumbers().forEach(treeName -> {
                        MeshtermTreePK treePK = new MeshtermTreePK(record.getDescriptorUI(), treeName.toString());
                        MeshtermTree meshtermTree = new MeshtermTree(treePK);
                        returnList.add(meshtermTree);
                    });
                }
                else {
                    logger.warn("No treeNameList for meshterm " + record.getDescriptorUI());
                }
            }
            else {
                logger.warn("No information for input ");
            }

            return returnList;
        }
    }

}
