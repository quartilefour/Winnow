package com.cscie599.gfn.ftp.downloader;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author PulkitBhanot
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.cscie599.gfn.ftp.downloader.*")
class IncrementalPubmedDownloadRunnableTest extends BasePubmedTest {

    @Test
    void getBaselineFilesNoFile() throws Exception {
        rawFolder.create();
        extractedFolder.create();
        rawFolder.newFolder("pubmed");

        CountDownLatch latch = new CountDownLatch(1);
        String localFilePath = rawFolder.getRoot().getPath() + File.separator + "pubmed";
        String extractedFilePath = extractedFolder.getRoot().getPath() + File.separator + "pubmed";

        new Thread(new TestIncrementalPubmedDownloadRunnable("ftp://abc.com", "/pubmed/baseline", "pubmed", localFilePath, latch, extractedFilePath, false)).start();
        latch.await(20, TimeUnit.SECONDS);
        File[] files = new File(localFilePath).listFiles();
        Assert.assertEquals(1033, files.length);
    }

    @Test
    void getBaselineFiles() throws Exception {
        rawFolder.create();
        extractedFolder.create();
        File pubmedFolder = rawFolder.newFolder("pubmed");

        for (int i = 1016; i < 1300; i++) {
            String fileIndex = String.format("%04d", i);
            String filePath = pubmedFolder.getAbsolutePath() + File.separator + "pubmed20n" + fileIndex + ".xml.gz";
            new File(filePath).createNewFile();
        }

        CountDownLatch latch = new CountDownLatch(1);
        String localFilePath = rawFolder.getRoot().getPath() + File.separator + "pubmed";
        String extractedFilePath = extractedFolder.getRoot().getPath() + File.separator + "pubmed";

        new Thread(new TestIncrementalPubmedDownloadRunnable("ftp://abc.com", "/pubmed/baseline", "pubmed", localFilePath, latch, extractedFilePath, false)).start();
        latch.await(20, TimeUnit.SECONDS);
        File[] files = new File(localFilePath).listFiles();
        Assert.assertEquals(1033, files.length);
    }

    @Test
    void getBaselineFilesNoNewFile() throws Exception {
        rawFolder.create();
        extractedFolder.create();
        File pubmedFolder = rawFolder.newFolder("pubmed");

        for (int i = 1016; i < 2048; i++) {
            String fileIndex = String.format("%04d", i);
            String filePath = pubmedFolder.getAbsolutePath() + File.separator + "pubmed20n" + fileIndex + ".xml.gz";
            new File(filePath).createNewFile();
        }

        CountDownLatch latch = new CountDownLatch(1);
        String localFilePath = rawFolder.getRoot().getPath() + File.separator + "pubmed";
        String extractedFilePath = extractedFolder.getRoot().getPath() + File.separator + "pubmed";

        new Thread(new TestIncrementalPubmedDownloadRunnable("ftp://abc.com", "/pubmed/baseline", "pubmed", localFilePath, latch, extractedFilePath, false)).start();
        latch.await(20, TimeUnit.SECONDS);
        File[] files = new File(localFilePath).listFiles();
        Assert.assertEquals(1033, files.length);
    }

    class TestIncrementalPubmedDownloadRunnable extends IncrementalPubmedDownloadRunnable {

        public TestIncrementalPubmedDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, CountDownLatch latch, String extractedFileLocation, boolean extractFiles) {
            super(ftpServerURL, ftpFilePath, fileName, localFilePath, latch, extractedFileLocation, extractFiles);
        }

        @VisibleForTesting
        public FTPClient getFtpClient() {
            return ftpClient;
        }

        boolean checkFileExists(String filePath, FTPClient ftpClient) throws IOException {
            return true;
        }
    }
}