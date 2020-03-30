package com.cscie599.gfn.controller;

import com.cscie599.gfn.service.IngestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Controller to launch the ingestion of pre downloaded datasets.
 *
 * @author PulkitBhanot
 */
@Controller
public class JobLauncherController {

    protected static final Log logger = LogFactory.getLog(JobLauncherController.class);

    @Autowired
    private IngestionService ingestionService;

    @RequestMapping("/jobLauncher.html")
    public ResponseEntity handle() throws Exception {
        boolean ingestionStatus = ingestionService.startIngestion();
        if(ingestionStatus)
            return new ResponseEntity<>("Job completed successfully!", HttpStatus.OK);
        else
            return new ResponseEntity<>("All Jobs didnot completed successfully!", HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
