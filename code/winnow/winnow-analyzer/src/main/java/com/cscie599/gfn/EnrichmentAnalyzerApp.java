package com.cscie599.gfn;

import com.cscie599.gfn.generator.ChiSquaredRunnable;
import com.cscie599.gfn.importer.analyzer.GeneRawStats;
import com.cscie599.gfn.importer.analyzer.MeshtermRawStats;
import com.cscie599.gfn.ingestor.analyzer.cache.InMemoryCache;
import com.cscie599.gfn.service.IngestionService;
import com.google.common.collect.Lists;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.javatuples.Pair;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.io.Resource;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/**
 * @author PulkitBhanot
 */
@SpringBootApplication
public class EnrichmentAnalyzerApp implements CommandLineRunner {

    protected static final Log logger = LogFactory.getLog(EnrichmentAnalyzerApp.class);

    @Autowired
    private IngestionService ingestionService;

    @Value("${analyzer.parallelism:10}")
    private int parallelism;

    @Value("${analyzer.includePairsWith0Publications:true}")
    private boolean includePairsWith0Publications;

    @Value("file:${input.directory}${output.gene_meshterm.file}")
    private Resource outputResource;

    @Autowired
    InMemoryCache inMemoryCache;

    public static void main(String[] args) {
        SpringApplication.run(EnrichmentAnalyzerApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {

        try {
            outputResource.getFile();
            logger.info("Starting ingestion of all files");
            boolean response = ingestionService.ingestDataForEnrichmentAnalysis();
            logger.info("Ingestion of all files completed successfully " + response);

            logger.info("Unique genes to process " + inMemoryCache.getCachedGeneStats().size());
            logger.info("Unique mesh to process " + inMemoryCache.getCachedMeshStats().size());
            logger.info("Unique Gene-Mesh pairs with overlapping publications " + inMemoryCache.getCachedGeneMeshPubStats().size());
            List<Pair<List<GeneRawStats>, List<MeshtermRawStats>>> partitionedPairs = getPartitionedPairsToProcess(parallelism);
            ExecutorService executorService = Executors.newFixedThreadPool(parallelism);
            CountDownLatch countDownLatch = new CountDownLatch(parallelism);
            for (int i = 0; i < parallelism; i++) {
                logger.info(">>>>" + partitionedPairs.get(i).getValue0().size() + ">>>>>>>" + partitionedPairs.get(i).getValue1().size());
                executorService.submit(new ChiSquaredRunnable(partitionedPairs.get(i), countDownLatch, i, includePairsWith0Publications, inMemoryCache.getCachedGeneMeshPubStats(), outputResource.getURI().getPath()));
            }
            countDownLatch.await(60, TimeUnit.MINUTES);
            if (response) {
                System.exit(0);
            } else {
                System.exit(1);
            }
        } catch (Exception ex) {
            logger.error("Error processing enrichment data", ex);
            System.exit(1);
        }
    }

    private List<Pair<List<GeneRawStats>, List<MeshtermRawStats>>> getPartitionedPairsToProcess(int n) {
        List<GeneRawStats> geneRawStats = Lists.newArrayList(inMemoryCache.getCachedGeneStats().values());
        List<MeshtermRawStats> meshRawStats = Lists.newArrayList(inMemoryCache.getCachedMeshStats().values());

        int geneBatchSize = (geneRawStats.size() + 100) / n;
        int meshBatchSize = (meshRawStats.size() + 100) / n;
        List<List<GeneRawStats>> partitionedGenes = Lists.partition(geneRawStats, geneBatchSize);
        List<List<MeshtermRawStats>> partitionedMesh = Lists.partition(meshRawStats, meshBatchSize);
        List<Pair<List<GeneRawStats>, List<MeshtermRawStats>>> returnPairList = new ArrayList<>(n);
        for (int i = 0; i < n; i++) {
            Pair<List<GeneRawStats>, List<MeshtermRawStats>> pair = new Pair<>(partitionedGenes.get(i), partitionedMesh.get(i));
            returnPairList.add(pair);
        }
        return returnPairList;
    }


}