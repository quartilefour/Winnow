package com.cscie599.gfn.ftp.downloader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author PulkitBhanot
 */
public class HTTPGeneOntologyDownloadRunnable implements Runnable {
    protected static final Log logger = LogFactory.getLog(HTTPGeneOntologyDownloadRunnable.class);

    private final String folderName;
    private final String extractedFileLocation;
    private final CountDownLatch latch;

    public HTTPGeneOntologyDownloadRunnable(String folderName,  CountDownLatch latch,String extractedFileLocation) {
        this.folderName = folderName;
        this.latch = latch;
        this.extractedFileLocation = extractedFileLocation;
    }

    @Override
    public void run() {

        try {
            String extractedFolderLocation = this.extractedFileLocation +"go";
            Path newDirPath = Paths.get(extractedFolderLocation);
            if (!Files.exists(newDirPath)) {
                Files.createDirectories(newDirPath);
            }

            FileUtils.copyURLToFile(
                    new URL("http://current.geneontology.org/ontology/subsets/goslim_agr.json"),
                    new File(extractedFolderLocation+"/goslim_agr.json"),
                    1000,
                    60000);

            FileUtils.copyURLToFile(
                    new URL("http://current.geneontology.org/ontology/subsets/goslim_generic.json"),
                    new File(extractedFolderLocation+"/goslim_generic.json"),
                    1000,
                    60000);

            FileUtils.copyURLToFile(
                    new URL("http://current.geneontology.org/ontology/subsets/goslim_mouse.json"),
                    new File(extractedFolderLocation+"/goslim_mouse.json"),
                    1000,
                    60000);

            FileUtils.copyURLToFile(
                    new URL("http://current.geneontology.org/ontology/subsets/goslim_plant.json"),
                    new File(extractedFolderLocation+"/goslim_plant.json"),
                    1000,
                    60000);
            latch.countDown();
        } catch (IOException e) {
            logger.error("Unable to successfully complete the processing of file " + this.folderName, e);

        }

    }
}

