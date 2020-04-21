package com.cscie599.gfn.service;

import com.google.common.collect.Sets;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author PulkitBhanot
 */
@Service
public class IngestionServiceImpl implements IngestionService {

    protected static final Log logger = LogFactory.getLog(IngestionServiceImpl.class);

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    List<Job> jobs;
    private static final String BASE_DATA_INGESTION = "BASE";
    private static final String ENRICHMENT_DATA_INGESTION = "ENRICHMENT";
    private static final String ENRICHED_DATA_INGESTION = "ENRICHED";

    private static Map<String, Set<String>> jobStageMap = new HashMap<>();

    static {
        jobStageMap.put(BASE_DATA_INGESTION, Sets.newHashSet("PubmedXMLIngester", "GoTermIngester", "GeneRelationshipIngester", "GeneGoTermIngester", "MeshtermIngestor", "GenePubmedIngester", "GeneInfoIngester"));
        jobStageMap.put(ENRICHMENT_DATA_INGESTION, Sets.newHashSet("MeshRawStatsIngester", "GeneRawStatsIngester", "GeneMeshPubStatsInMemoryIngester"));
        jobStageMap.put(ENRICHED_DATA_INGESTION, Sets.newHashSet("MeshRawStatsIngester", "GeneRawStatsIngester",  "GeneMeshtermIngestor", "GeneAssociationIngestor"));
    }

    @Override
    public boolean ingestBaseData() {
        return processIngestionInternal(jobStageMap.get(BASE_DATA_INGESTION));
    }

    private boolean processIngestionInternal(Set<String> jobName) {
        logger.info("Starting the processing of different jobs");
        AtomicBoolean processedAll = new AtomicBoolean(true);
        JobParameters jobParameters =
                new JobParametersBuilder().addString("date", "" + System.currentTimeMillis()).toJobParameters();
        jobs.forEach((job -> {
            if (jobName.contains(job.getName())) {
                try {
                    logger.info("Starting the processing of job:- " + job.getName());
                    long startTime = System.currentTimeMillis();
                    JobExecution jobExecution = jobLauncher.run(job, jobParameters);
                    jobExecution.getStepExecutions().forEach(action -> {
                        logger.info(" status for " + action.getStepName() + " readCount " + action.getReadCount() + " commitCount " + action.getCommitCount() + " writeCount " + action.getWriteCount() + " writeSkipCount " + action.getWriteSkipCount() + " rollbackCount " + action.getRollbackCount() + " failureCount " + action.getFilterCount());
                    });
                    logger.info("Finished processing of job:- " + job.getName() + " timeTakenInMs:- " + (System.currentTimeMillis() - startTime));
                } catch (Exception ex) {
                    logger.error("Unable to complete processing of all the jobs", ex);
                    processedAll.set(false);
                }
            } else {
                logger.info("Job " + job.getName() + " is not configured for this run");
            }
        }));
        logger.info("Processing of all jobs finished");
        return processedAll.get();
    }

    @Override
    public boolean ingestDataForEnrichmentAnalysis() {
        return processIngestionInternal(jobStageMap.get(ENRICHMENT_DATA_INGESTION));

    }

    @Override
    public boolean ingestEnrichedData() {
        return processIngestionInternal(jobStageMap.get(ENRICHED_DATA_INGESTION));
    }
}
