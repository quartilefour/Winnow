package com.cscie599.gfn;

import com.cscie599.gfn.service.FileService;
import com.cscie599.gfn.service.IngestionService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Springboot application that ingests only the basedata i.e datasets that are publicly available on the internet.
 *
 * @author PulkitBhanot
 */
@SpringBootApplication
public class BulkIngesterApp implements CommandLineRunner {
    protected static final Log logger = LogFactory.getLog(BulkIngesterApp.class);

    @Autowired
    private IngestionService ingestionService;

    @Autowired
    private FileService fileService;

    // Property that determines whether the app should ingest basedata.
    @Value("${ingester.baseDataIngestion:true}")
    private boolean baseDataIngestion;

    // Property that determines whether the app should ingest deriveddata.
    @Value("${ingester.derivedDataIngestion:false}")
    private boolean derivedDataIngestion;

    // Property that overrides the analyser ingester behavior and configures it to write to db
    @Value("${input.StatsIngester.inMemory}")
    private boolean inMemory;

    public static void main(String[] args) {
        SpringApplication.run(BulkIngesterApp.class, args);
    }

    @Override
    public void run(String... args) throws Exception {
        logger.info("Starting ingestion of all files");
        boolean response = false;
        if (baseDataIngestion) {
            logger.info("Starting ingestion of base data");
            response = ingestionService.ingestBaseData();
            logger.info("Completed ingestion of base data");
            logger.info("Starting splitting of mesh_publication dataset");
            fileService.splitAndZipFiles();
            logger.info("Completed splitting of mesh_publication dataset");
        }
        if (derivedDataIngestion) {
            if (!inMemory) {
                logger.info("Starting ingestion of derived data");
                response = ingestionService.ingestEnrichedData();
                logger.info("Completed ingestion of derived data");
            } else {
                logger.error("Wrong configuration detected derived dataset cannot be ingested if property input.StatsIngester.inMemory is true");
                response = false;
            }
        }
        logger.info("Ingestion of all files completed successfully " + response);
        if (response) {
            System.exit(0);
        } else {
            System.exit(1);
        }
    }
}
