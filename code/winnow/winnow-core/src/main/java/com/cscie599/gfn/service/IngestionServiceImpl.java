package com.cscie599.gfn.service;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
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

    @Override
    public boolean startIngestion() {
        logger.info("Starting the processing of different jobs");
        AtomicBoolean processedAll = new AtomicBoolean(true);
        JobParameters jobParameters =
                new JobParametersBuilder().addString("date", "" + System.currentTimeMillis()).toJobParameters();
        jobs.forEach((job -> {
            try {
                logger.info("Starting the processing of job:- " + job.getName());
                long startTime = System.currentTimeMillis();
                JobExecution jobExecution = jobLauncher.run(job, jobParameters);
                jobExecution.getStepExecutions().forEach(action -> {
                    logger.info(" status for " + action.getStepName() + " readCount " + action.getReadCount() + " commitCount " + action.getCommitCount() + " writeCount " + action.getWriteCount() + " writeSkipCount " + action.getWriteSkipCount() + " rollbackCount " + action.getRollbackCount() + " failureCount " + action.getFilterCount());
                });
                logger.info("Finished processing of job:- " + job.getName() + " timeTakenInMs:- " + (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                logger.error("Unable to complete processing of all the jobs", e);
                processedAll.set(false);
            }
        }));
        logger.info("Processing of all jobs finished");
        return processedAll.get();
    }
}
