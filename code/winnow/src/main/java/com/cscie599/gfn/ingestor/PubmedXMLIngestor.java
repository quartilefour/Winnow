package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.importer.pubmed.converter.MeshHeadingConverter;
import com.cscie599.gfn.importer.pubmed.converter.PMIDConverter;
import com.cscie599.gfn.entities.*;
import com.cscie599.gfn.importer.pubmed.PubmedArticle;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.support.CompositeItemWriter;
import org.springframework.batch.item.xml.StaxEventItemReader;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.oxm.xstream.XStreamMarshaller;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;

@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class PubmedXMLIngestor {

    @Autowired
    private JobBuilderFactory jobBuilderFactory;

    @Autowired
    private StepBuilderFactory stepBuilderFactory;

    @Autowired
    DataSource dataSource;

    @Value("file:${input.pubmed.file}")
    private Resource inputResource;

    @Bean(name = "stepPubMedInfo")
    public Step stepGeneInfo() {
        return stepBuilderFactory
                .get("step")
                .<PubmedArticle,List<Object>>chunk(5)
                .reader(readerForPubmed())
                .processor(processorForAuthors())
                .writer(new MultiOutputItemWriter(authorWriter1(), publicationWriter2(), authorPublicationWriter3()))
                /*.processor(processorForArticles())
                .writer(writerForArticles())*/
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .skipLimit(50000)
                .build();
    }

    private ItemProcessor<PubmedArticle,List<Object>> processorForAuthors() {
        return new DBLogProcessor();
    }

    private ItemWriter<? super Object> writerForArticles() {
        return null;
    }

    private ItemProcessor<? super PubmedArticle, ?> processorForArticles() {
        return null;
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
        JdbcBatchItemWriter<Author> itemWriter = new JdbcBatchItemWriter<Author>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO author (author_id, fore_name,last_name) VALUES (:authorId, :foreName,:lastName) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Author>());
        return itemWriter;
    }
    @Bean
    public JdbcBatchItemWriter<Publication> publicationWriter2() {
        JdbcBatchItemWriter<Publication> itemWriter = new JdbcBatchItemWriter<Publication>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO publication (publication_id) VALUES (:publicationId) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Publication>());
        return itemWriter;
    }

    @Bean
    public JdbcBatchItemWriter<PublicationAuthor> authorPublicationWriter3() {
        JdbcBatchItemWriter<PublicationAuthor> itemWriter = new JdbcBatchItemWriter<PublicationAuthor>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO publication_author (author_id, publication_id) VALUES (:publicationAuthorPK.authorId, :publicationAuthorPK.publicationId) ON CONFLICT DO NOTHING");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<PublicationAuthor>());
        return itemWriter;
    }
    public class MultiOutputItemWriter implements ItemWriter<Object> {

        private JdbcBatchItemWriter<Author> delegateAuthor;
        private JdbcBatchItemWriter<Publication> delegatePublication;
        private JdbcBatchItemWriter<PublicationAuthor> delegatePublicationAuthor;

        public MultiOutputItemWriter(JdbcBatchItemWriter<Author> delegateAuthor, JdbcBatchItemWriter<Publication> delegatePublication,JdbcBatchItemWriter<PublicationAuthor> delegatePublicationAuthor) {
            this.delegateAuthor = delegateAuthor;
            this.delegatePublication = delegatePublication;
            this.delegatePublicationAuthor = delegatePublicationAuthor;
        }

        public void write(List<? extends Object> items) throws Exception {
            List<Author> authors = new ArrayList<>();
            List<Publication> publications = new ArrayList<>();
            List<PublicationAuthor> publicationAuthors = new ArrayList<>();

            ((List)items.get(0)).forEach(item -> {
                if(item.getClass().equals(Author.class)){
                    authors.add((Author) item);
                }else if(item.getClass().equals(Publication.class)){
                    publications.add((Publication) item);
                }else if(item.getClass().equals(PublicationAuthor.class)){
                    publicationAuthors.add((PublicationAuthor) item);
                }
            });
            delegateAuthor.write(authors);
            delegatePublication.write(publications);
            delegatePublicationAuthor.write(publicationAuthors);
        }
    }


    @Bean
    public StaxEventItemReader<PubmedArticle> readerForPubmed(){
        StaxEventItemReader<PubmedArticle> reader = new StaxEventItemReader<PubmedArticle>();
        reader.setResource(inputResource);
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

        return reader;
    }

    class DBLogProcessor implements ItemProcessor<PubmedArticle, List<Object>>
    {
        public List<Object> process(PubmedArticle pubmedArticle) throws Exception
        {
            List<Object> returnList = new ArrayList<>();
            PubmedArticle article = ((PubmedArticle)pubmedArticle);
            if(article.getMedlineCitation() != null && article.getMedlineCitation().getArticle() != null && article.getMedlineCitation().getArticle().getAuthorList() != null){
                article.getMedlineCitation().getArticle().getAuthorList().forEach((author -> {
                    Author author1 = new Author();
                    author1.setAuthorId(author.getLastName() ==null ? "" :author.getLastName().toLowerCase()+"-"+author.getForeName().toLowerCase());
                    author1.setForeName(author.getForeName());
                    author1.setLastName(author.getLastName());
                    returnList.add(author1);
                    PublicationAuthorPK publicationAuthorPK = new PublicationAuthorPK(pubmedArticle.getMedlineCitation().getPMID().getID(),author1.getAuthorId());
                    PublicationAuthor publicationAuthor = new PublicationAuthor(publicationAuthorPK);
                    returnList.add(publicationAuthor);
                }));
            }else{
                System.err.println("No author list for article "+article.getMedlineCitation().getPMID().getID());
            }

            Publication publication = new Publication();
            publication.setPublicationId(pubmedArticle.getMedlineCitation().getPMID().getID());
            returnList.add(publication);
            System.out.println("Inserting Pubmed : " + pubmedArticle);
            return returnList;
        }
    }
}
