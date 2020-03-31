package com.cscie599.gfn.ingestor;

import com.cscie599.gfn.entities.Goterm;
import com.cscie599.gfn.importer.goterm.Root;
import com.cscie599.gfn.ingestor.writer.UpsertableJdbcBatchItemWriter;
import lombok.RequiredArgsConstructor;
import lombok.experimental.Delegate;
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
import org.springframework.batch.item.file.MultiResourceItemReader;
import org.springframework.batch.item.json.JacksonJsonObjectReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.core.io.InputStreamSource;
import org.springframework.core.io.Resource;
import org.springframework.dao.EmptyResultDataAccessException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 * @author PulkitBhanot
 */
@Configuration
@EnableBatchProcessing
@EnableAutoConfiguration
public class GoTermIngester extends BaseIngester {

    protected static final Log logger = LogFactory.getLog(GoTermIngester.class);

    @Value("file:${input.directory}${input.gene-goslim.file}")
    private Resource[] inputResources;

    @Value("${input.GoTermIngester.skipLines:0}")
    private int linesToSkip;

    @Bean
    @Order(3)
    public Job getGoTermIngester() {
        return jobBuilderFactory.get("GoTermIngester")
                .start(stepGoTerm())
                .build();
    }

    @Bean(name = "stepGoTerm")
    public Step stepGoTerm() {
        return stepBuilderFactory
                .get("stepGoTerm")
                .<Root, List<Object>>chunk(ingestionBatchSize)
                .reader(readerForGoTerm())
                .processor(processorForGoTerm())
                .writer(new SingleOutputItemWriter(goTermWriter()))
                .faultTolerant()
                .skip(EmptyResultDataAccessException.class)
                .noRetry(EmptyResultDataAccessException.class)
                .noRollback(EmptyResultDataAccessException.class)
                .skipLimit(ingestionSkipLimit)
                .transactionAttribute(IngeterUtil.getDefaultTransactionAttribute())
                .build();
    }

    private ItemProcessor<Root, List<Object>> processorForGoTerm() {
        return new DBLogProcessor();
    }

    @Bean
    public JdbcBatchItemWriter<Goterm> goTermWriter() {
        JdbcBatchItemWriter<Goterm> itemWriter = new UpsertableJdbcBatchItemWriter<>();
        itemWriter.setDataSource(dataSource);
        itemWriter.setSql("INSERT INTO goterm (go_id, definition,xrefs,label) VALUES (:goId, :definition,:xrefs,:label) ON CONFLICT DO NOTHING RETURNING go_id");
        itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<Goterm>());
        return itemWriter;
    }

    public class SingleOutputItemWriter implements ItemWriter<Object> {

        private JdbcBatchItemWriter<Goterm> goTermWriter;

        public SingleOutputItemWriter(JdbcBatchItemWriter<Goterm> goTermWriter) {
            this.goTermWriter = goTermWriter;
        }

        public void write(List<? extends Object> items) throws Exception {
            goTermWriter.write((List<? extends Goterm>) items.get(0));
        }
    }

    @Bean
    public ItemReader<Root> readerForGoTerm() {
        //https://stackoverflow.com/questions/55791452/unmarshalling-with-jackson-the-json-input-stream-must-start-with-an-array-of-js
        logger.info("Reading resource: " + inputResources + " for " + this.getClass().getName());
        CustomJsonItemReader<Root> delegate = new CustomJsonItemReader<>();
        delegate.setJsonObjectReader(new JacksonJsonObjectReader<>(Root.class));
        delegate.setSaveState(false);
        delegate.setStrict(true);
        MultiResourceItemReader<Root> reader = new MultiResourceItemReader<>();
        reader.setResources(Arrays.stream(inputResources)
                .map(WrappedResource::new) // forcing the bride to look good enough
                .toArray(Resource[]::new));
        reader.setDelegate(new GZResourceAwareItemReaderItemStream(delegate, false));
        return reader;

    }

    @RequiredArgsConstructor
    static class WrappedResource implements Resource {
        @Delegate(excludes = InputStreamSource.class)
        private final Resource resource;

        @Override
        public InputStream getInputStream() throws IOException {
            logger.info("Wrapping resource: {}" + resource.getFilename());
            InputStream in = resource.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String wrap = reader.lines().collect(Collectors.joining())
                    .replaceAll("[^\\x00-\\xFF]", "");  // strips off all non-ASCII characters
            return new ByteArrayInputStream(("[" + wrap + "]").getBytes(StandardCharsets.UTF_8));
        }
    }

    class DBLogProcessor implements ItemProcessor<Root, List<Object>> {
        public List<Object> process(Root rootOfGraph) throws Exception {
            List<Object> returnList = new ArrayList<>();
            if (rootOfGraph.getGraphs() != null && rootOfGraph.getGraphs().size() > 0) {
                Root.Graphs firstGraph = rootOfGraph.getGraphs().get(0);
                firstGraph.getNodes().forEach(node -> {
                    Goterm goterm = new Goterm();
                    String goID = node.getId().substring(node.getId().lastIndexOf('/') + 1);
                    if (goID.startsWith("GO") || goID.startsWith("RO")) {
                        goterm.setGoId(node.getId().substring(node.getId().lastIndexOf('/') + 1));
                        if (logger.isDebugEnabled()) {
                            logger.debug("Inserting goterm" + goterm.getGoId());

                        }
                        if (node.getMeta() != null && node.getMeta().getDefinition() != null) {
                            if (node.getMeta().getDefinition().getVal() != null) {
                                goterm.setDefinition(node.getMeta().getDefinition().getVal());
                            }
                            if (node.getMeta().getDefinition().getXrefs() != null) {
                                goterm.setXrefs(node.getMeta().getDefinition().getXrefs().toString());
                            }
                        }
                        goterm.setLabel(node.getLbl());
                        returnList.add(goterm);
                    }
                });
            }
            return returnList;
        }
    }
}
