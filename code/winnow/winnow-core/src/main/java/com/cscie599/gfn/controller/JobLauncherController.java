package com.cscie599.gfn.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

/**
 * Controller to launch the ingestion of pre downloaded datasets.
 *
 * @author PulkitBhanot
 */
@Controller
public class JobLauncherController {

    protected static final Log logger = LogFactory.getLog(JobLauncherController.class);

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    List<Job> jobs;

    @RequestMapping("/jobLauncher.html")
    public ResponseEntity handle() throws Exception {
        logger.info("Starting the processing of different jobs");
        JobParameters jobParameters =
                new JobParametersBuilder().addString("date", "" + System.currentTimeMillis()).toJobParameters();
        jobs.forEach((job -> {
            try {
                logger.info("Starting the processing of job:- " + job.getName());
                long startTime = System.currentTimeMillis();
                jobLauncher.run(job, jobParameters);
                logger.info("Finished processing of job:- " + job.getName() + " timeTakenInMs:- " + (System.currentTimeMillis() - startTime));
            } catch (Exception e) {
                logger.error("Unable to complete processing of all the jobs", e);
            }
        }));
        logger.info("Processing of all jobs finished");
        return new ResponseEntity<>("Job completed successfully!", HttpStatus.OK);
    }
}
