package com.cscie599.gfn.controller;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * Controller to launch the ingestion of pre downloaded datasets.
 * @author PulkitBhanot
 */
@RestController
@RequestMapping("/api")
public class StatusController {

    protected static final Log logger = LogFactory.getLog(StatusController.class);

    @GetMapping("/status")
    public ResponseEntity<?> status() {
        return new ResponseEntity<>("Winnow API available", HttpStatus.OK);
    }
}
