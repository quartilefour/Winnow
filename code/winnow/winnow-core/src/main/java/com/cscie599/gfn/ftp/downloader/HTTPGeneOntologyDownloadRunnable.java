package com.cscie599.gfn.ftp.downloader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author PulkitBhanot
 */
public class HTTPGeneOntologyDownloadRunnable implements Runnable {
    protected static final Log logger = LogFactory.getLog(HTTPGeneOntologyDownloadRunnable.class);

    private final String folderName;
    private final String localFilePath;
    private final CountDownLatch latch;

    public HTTPGeneOntologyDownloadRunnable(String folderName, String localFilePath, CountDownLatch latch) {
        this.folderName = folderName;
        this.localFilePath = localFilePath;
        this.latch = latch;
    }

    @Override
    public void run() {
        try {
            FileUtils.copyURLToFile(
                    new URL("http://current.geneontology.org/ontology/subsets/goslim_agr.json"),
                    new File("/tmp/data/go/goslim_agr.json"),
                    1000,
                    60000);

            FileUtils.copyURLToFile(
                    new URL("http://current.geneontology.org/ontology/subsets/goslim_generic.json"),
                    new File("/tmp/data/go/goslim_generic.json"),
                    1000,
                    60000);

            FileUtils.copyURLToFile(
                    new URL("http://current.geneontology.org/ontology/subsets/goslim_mouse.json"),
                    new File("/tmp/data/go/goslim_mouse.json"),
                    1000,
                    60000);

            FileUtils.copyURLToFile(
                    new URL("http://current.geneontology.org/ontology/subsets/goslim_plant.json"),
                    new File("/tmp/data/go/goslim_plant.json"),
                    1000,
                    60000);
            latch.countDown();
        } catch (IOException e) {
            logger.error("Unable to successfully complete the processing of file " + this.folderName, e);

        }

    }
}

