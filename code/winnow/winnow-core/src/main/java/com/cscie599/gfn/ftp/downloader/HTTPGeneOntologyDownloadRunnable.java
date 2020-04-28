package com.cscie599.gfn.ftp.downloader;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.apache.commons.io.FilenameUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.concurrent.CountDownLatch;

/**
 * @author PulkitBhanot
 */
public class HTTPGeneOntologyDownloadRunnable implements Runnable {
    protected static final Log logger = LogFactory.getLog(HTTPGeneOntologyDownloadRunnable.class);

    private final String folderName;
    private final String extractedFileLocation;
    private final String rawFileLocation;
    private final boolean extractContent;
    private static final String GO_URL = "http://current.geneontology.org/ontology/subsets/";

    private final CountDownLatch latch;

    public HTTPGeneOntologyDownloadRunnable(String folderName, CountDownLatch latch, String extractedFileLocation, String rawFileLocation, Boolean extractContent) {
        this.folderName = folderName;
        this.latch = latch;
        this.extractedFileLocation = extractedFileLocation;
        this.rawFileLocation = rawFileLocation;
        this.extractContent = extractContent;
    }

    @Override
    public void run() {

        try {
            String extractedFolderLocation = this.extractedFileLocation + "gene_ontology";
            Path newDirPath = Paths.get(extractedFolderLocation);
            if (!Files.exists(newDirPath)) {
                Files.createDirectories(newDirPath);
            }

            String rawFolderLocation = this.rawFileLocation + "gene_ontology";
            newDirPath = Paths.get(rawFolderLocation);
            if (!Files.exists(newDirPath)) {
                Files.createDirectories(newDirPath);
            }

            Document doc = Jsoup.connect(GO_URL).get();
            Elements links = doc.select("a");
            links.stream().forEach(element -> {
                String href = element.attr("href");
                if (href.endsWith(".json") && href.contains("goslim")) {
                    String fileName = FilenameUtils.getName(href);
                    logger.info("Processing file " + fileName + " from path " + href);
                    try {
                        FileUtils.copyURLToFile(
                                new URL(href),
                                new File(rawFolderLocation + File.separator + fileName),
                                1000,
                                60000);
                        if (extractContent) {
                            FileUtils.copyFile(new File(rawFolderLocation + File.separator + fileName), new File(extractedFolderLocation + File.separator + fileName));
                        }
                    } catch (IOException e) {
                        logger.error("Unable to successfully download file");
                    }
                }
            });

        } catch (IOException e) {
            logger.error("Unable to successfully complete the processing of file " + this.folderName, e);
        } finally {
            latch.countDown();
        }
    }
}

