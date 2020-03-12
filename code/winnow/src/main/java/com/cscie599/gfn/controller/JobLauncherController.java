package com.cscie599.gfn.controller;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class JobLauncherController {

    @Autowired
    JobLauncher jobLauncher;

    @Autowired
    Job job;

    @RequestMapping("/jobLauncher.html")
    public ResponseEntity handle() throws Exception{
        JobParameters jobParameters =
                new JobParametersBuilder().addString("date",""+System.currentTimeMillis()).toJobParameters();
        jobLauncher.run(job, jobParameters);
        return new ResponseEntity<>("Job completed successfully!", HttpStatus.OK);
    }
}
