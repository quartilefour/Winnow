package com.cscie599.gfn.ftp.downloader;

import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author PulkitBhanot
 */
class HTTPGeneOntologyDownloadRunnableTest {

    @Rule
    public TemporaryFolder rawFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder extractedFolder = new TemporaryFolder();

    @BeforeEach
    void setUp() throws IOException {
        rawFolder.create();
        extractedFolder.create();
    }

    @Test
    void testHTTPRun() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        String folderPath = rawFolder.getRoot().getAbsolutePath() + File.separator;
        String downloadFolderName = "gene_ontology";
        HTTPGeneOntologyDownloadRunnable runnable = new HTTPGeneOntologyDownloadRunnable(downloadFolderName, latch, folderPath, folderPath, false);
        new Thread(runnable).start();

        latch.await(5, TimeUnit.MINUTES);
        File downloadFolder = new File(folderPath + downloadFolderName);
        assertTrue(downloadFolder.exists());
        assertTrue(downloadFolder.isDirectory());
        for (File f1 : downloadFolder.listFiles()) {
            assertTrue(f1.isFile());
            assertTrue(f1.getName().contains("goslim"));
        }
    }

    @Test
    void testHTTPRunWithExtract() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        String folderPath = rawFolder.getRoot().getAbsolutePath() + File.separator;
        String downloadFolderName = "gene_ontology";
        String extractFolderName = extractedFolder.getRoot().getAbsolutePath() + File.separator;
        HTTPGeneOntologyDownloadRunnable runnable = new HTTPGeneOntologyDownloadRunnable(downloadFolderName, latch, extractFolderName, folderPath, false);
        new Thread(runnable).start();

        latch.await(5, TimeUnit.MINUTES);
        File downloadFolder = new File(extractFolderName + downloadFolderName);
        assertTrue(downloadFolder.exists());
        assertTrue(downloadFolder.isDirectory());
        for (File f1 : downloadFolder.listFiles()) {
            assertTrue(f1.isFile());
            assertTrue(f1.getName().contains("goslim"));
        }
    }
}