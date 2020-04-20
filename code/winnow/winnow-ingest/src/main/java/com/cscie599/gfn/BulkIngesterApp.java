package com.cscie599.gfn;

import com.cscie599.gfn.service.IngestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class BulkIngesterApp implements CommandLineRunner {
    protected static final Log logger = LogFactory.getLog(BulkIngesterApp.class);

    @Autowired
    private IngestionService ingestionService;

    public static void main(String[] args) {
        SpringApplication.run(BulkIngesterApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting ingestion of all files");
        boolean response = ingestionService.ingestBaseData();
        logger.info("Ingestion of all files completed successfully " + response);
        if (response) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
