package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.*;
import com.cscie599.gfn.importer.pubmed.PubmedArticle;
import com.cscie599.gfn.importer.pubmed.converter.MeshHeadingConverter;
import com.cscie599.gfn.importer.pubmed.converter.PMIDConverter;
import com.cscie599.gfn.ingestor.reader.SkipSupportedMultiResourceItemReader;
import com.cscie599.gfn.ingestor.writer.UpsertableJdbcBatchItemWriter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
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

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @author PulkitBhanot
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class PubmedXMLIngester extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(PubmedXMLIngester.class);

    @Value("file:${input.directory}${input.pubmed.file}")
    private Resource[] inputResources;

    @Value("${input.PubmedXMLIngester.skipLines:0}")
    private int linesToSkip;

    @Bean
    @Order(7)
    public Job getPubmedXMLIngester() {
        return jobBuilderFactory.get("PubmedXMLIngester")
                .start(stepPubMedInfo())
                .build();
    }

    @Bean(name = "stepPubMedInfo")
    public Step stepPubMedInfo() {
        return stepBuilderFactory
                .get("stepPubMedInfo")
                .<PubmedArticle, List<Object>>chunk(1)
                .reader(readerForPubmed())
                .processor(processorForAuthors())
                .writer(new MultiOutputItemWriter(authorWriter1(), publicationWriter2(), authorPublicationWriter3(), publicationMeshWriter4()))
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(ingestionSkipLimit)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    private ItemProcessor<PubmedArticle, List<Object>> processorForAuthors() {
        return new DBLogProcessor();
    }

    @Bean
    public CompositeItemWriter compositeItemWriter() {
        List<ItemWriter> writers = new ArrayList<>(2);
        writers.add(authorWriter1());
        writers.add(publicationWriter2());
        CompositeItemWriter itemWriter = new CompositeItemWriter();
        itemWriter.setDelegates(writers);
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<Author> authorWriter1() {
        JdbcBatchItemWriter<Author> itemWriter = new UpsertableJdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO author (author_id, fore_name,last_name) VALUES (:authorId, :foreName,:lastName) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Author>());
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<Publication> publicationWriter2() {
        JdbcBatchItemWriter<Publication> itemWriter = new UpsertableJdbcBatchItemWriter<Publication>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO publication (publication_id,completed_date, date_revised, title, language ) VALUES (:publicationId, :completedDate, :dateRevised, :title, :language) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Publication>());
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<PublicationAuthor> authorPublicationWriter3() {
        JdbcBatchItemWriter<PublicationAuthor> itemWriter = new UpsertableJdbcBatchItemWriter<PublicationAuthor>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO publication_author (author_id, publication_id) VALUES (:publicationAuthorPK.authorId, :publicationAuthorPK.publicationId) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<PublicationAuthor>());
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<PublicationMeshterm> publicationMeshWriter4() {
        JdbcBatchItemWriter<PublicationMeshterm> itemWriter = new UpsertableJdbcBatchItemWriter<PublicationMeshterm>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO publication_meshterm (mesh_id, publication_id, created_date) VALUES (:publicationMeshtermPK.meshId, :publicationMeshtermPK.publicationId, :createdDate) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<PublicationMeshterm>());
        return itemWriter;
    }

    public class MultiOutputItemWriter implements ItemWriter<Object> {

        private JdbcBatchItemWriter<Author> delegateAuthor;
        private JdbcBatchItemWriter<Publication> delegatePublication;
        private JdbcBatchItemWriter<PublicationAuthor> delegatePublicationAuthor;
        private JdbcBatchItemWriter<PublicationMeshterm> delegatePublicationMeshTerm;

        public MultiOutputItemWriter(JdbcBatchItemWriter<Author> delegateAuthor, JdbcBatchItemWriter<Publication> delegatePublication, JdbcBatchItemWriter<PublicationAuthor> delegatePublicationAuthor, JdbcBatchItemWriter<PublicationMeshterm> delegatePublicationMeshTerm) {
            this.delegateAuthor = delegateAuthor;
            this.delegatePublication = delegatePublication;
            this.delegatePublicationAuthor = delegatePublicationAuthor;
            this.delegatePublicationMeshTerm = delegatePublicationMeshTerm;
        }

        @Transactional(isolation = Isolation.SERIALIZABLE)
        public void write(List<? extends Object> items) throws Exception {
            List<Author> authors = new ArrayList<>();
            List<Publication> publications = new ArrayList<>();
            List<PublicationAuthor> publicationAuthors = new ArrayList<>();
            List<PublicationMeshterm> publicationMeshterms = new ArrayList<>();

            ((List) items.get(0)).forEach(item -> {
                if (item.getClass().equals(Author.class)) {
                    authors.add((Author) item);
                } else if (item.getClass().equals(Publication.class)) {
                    publications.add((Publication) item);
                } else if (item.getClass().equals(PublicationAuthor.class)) {
                    publicationAuthors.add((PublicationAuthor) item);
                } else if (item.getClass().equals(PublicationMeshterm.class)) {
                    publicationMeshterms.add((PublicationMeshterm) item);
                }
            });
            delegateAuthor.write(authors);
            delegatePublication.write(publications);
            delegatePublicationAuthor.write(publicationAuthors);
            delegatePublicationMeshTerm.write(publicationMeshterms);
        }
    }


    @Bean
    public ItemReader<PubmedArticle> readerForPubmed() {
        logger.info("Reading resource: " + inputResources + " for " + this.getClass().getName());
        SkipSupportedMultiResourceItemReader<PubmedArticle> multiResourceItemReader = new SkipSupportedMultiResourceItemReader<PubmedArticle>();
        multiResourceItemReader.setResources(inputResources);

        StaxEventItemReader<PubmedArticle> reader = new StaxEventItemReader<PubmedArticle>();
        multiResourceItemReader.setDelegate(new GZResourceAwareItemReaderItemStream(reader, useZippedFormat));
        reader.setFragmentRootElementName("PubmedArticle");
        XStreamMarshaller xStreamMarshaller = new XStreamMarshaller();
        xStreamMarshaller.getXStream().ignoreUnknownElements();
        xStreamMarshaller.getXStream().alias("MeshHeading", PubmedArticle.MeshHeading.class);
        xStreamMarshaller.getXStream().alias("PubmedArticle", PubmedArticle.class);
        xStreamMarshaller.getXStream().alias("MedlineCitation", PubmedArticle.MedlineCitation.class);
        xStreamMarshaller.getXStream().alias("Author", PubmedArticle.Author.class);
        xStreamMarshaller.getXStream().alias("Chemical", PubmedArticle.Chemical.class);
        xStreamMarshaller.getXStream().alias("MeshHeading", PubmedArticle.MeshHeading.class);
        xStreamMarshaller.getXStream().alias("PMID", PubmedArticle.PMID.class);
        xStreamMarshaller.getXStream().registerConverter(new MeshHeadingConverter());
        xStreamMarshaller.getXStream().registerConverter(new PMIDConverter());
        reader.setUnmarshaller(xStreamMarshaller);
        multiResourceItemReader.setLinesToSkip(linesToSkip);
        return multiResourceItemReader;
    }

    class DBLogProcessor implements ItemProcessor<PubmedArticle, List<Object>> {
        public List<Object> process(PubmedArticle pubmedArticle) throws Exception {
            List<Object> returnList = new ArrayList<>();
            PubmedArticle article = ((PubmedArticle) pubmedArticle);
            if (article.getMedlineCitation() != null && article.getMedlineCitation().getArticle() != null && article.getMedlineCitation().getArticle().getAuthorList() != null) {
                article.getMedlineCitation().getArticle().getAuthorList().forEach((author -> {
                    Author author1 = new Author();
                    author1.setAuthorId(author.getLastName() == null ? "" : author.getLastName().toLowerCase() + "-" + author.getForeName().toLowerCase());
                    author1.setForeName(author.getForeName());
                    author1.setLastName(author.getLastName());
                    returnList.add(author1);
                    PublicationAuthorPK publicationAuthorPK = new PublicationAuthorPK(pubmedArticle.getMedlineCitation().getPMID().getID(), author1.getAuthorId());
                    PublicationAuthor publicationAuthor = new PublicationAuthor(publicationAuthorPK);
                    returnList.add(publicationAuthor);
                }));
            } else {
                logger.warn("No author list for article " + article.getMedlineCitation().getPMID().getID());
            }

            if (article.getMedlineCitation() != null && article.getMedlineCitation().getMeshHeadingList() != null) {
                article.getMedlineCitation().getMeshHeadingList().forEach(meshHeading -> {
                    meshHeading.getDescriptorName().forEach(descriptor -> {
                        PublicationMeshtermPK publicationMeshtermPK = new PublicationMeshtermPK(pubmedArticle.getMedlineCitation().getPMID().getID(), descriptor.getUI());
                        PublicationMeshterm publicationMeshterm = new PublicationMeshterm(publicationMeshtermPK);
                        publicationMeshterm.setCreatedDate(new Date());
                        returnList.add(publicationMeshterm);
                    });
                });
            }
            Publication publication = new Publication();
            publication.setPublicationId(pubmedArticle.getMedlineCitation().getPMID().getID());
            publication.setTitle(pubmedArticle.getMedlineCitation().getArticle().getArticleTitle());
            publication.setLanguage(pubmedArticle.getMedlineCitation().getArticle().getLanguage());
            updateDates(publication, pubmedArticle);
            returnList.add(publication);
            return returnList;
        }
    }

    private void updateDates(Publication publication, PubmedArticle pubmedArticle) {
        if (pubmedArticle.getMedlineCitation().getDateCompleted() != null) {
            PubmedArticle.DateCompleted dateCompleted = pubmedArticle.getMedlineCitation().getDateCompleted();
            publication.setCompletedDate(new Date(Integer.parseInt(dateCompleted.getYear()), Integer.parseInt(dateCompleted.getMonth()), Integer.parseInt(dateCompleted.getDay())));
        }
        if (pubmedArticle.getMedlineCitation().getDateRevised() != null) {
            PubmedArticle.DateRevised dateRevised = pubmedArticle.getMedlineCitation().getDateRevised();
            publication.setDateRevised(new Date(Integer.parseInt(dateRevised.getYear()), Integer.parseInt(dateRevised.getMonth()), Integer.parseInt(dateRevised.getDay())));
        }
    }
}
