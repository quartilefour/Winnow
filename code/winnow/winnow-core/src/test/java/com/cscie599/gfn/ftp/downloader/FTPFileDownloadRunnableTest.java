package com.cscie599.gfn.ftp.downloader;

import com.google.common.annotations.VisibleForTesting;
import org.apache.commons.net.ftp.FTPClient;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.powermock.api.mockito.PowerMockito.when;

/**
 * @author PulkitBhanot
 */
@RunWith(PowerMockRunner.class)
@PrepareForTest(fullyQualifiedNames = "com.cscie599.gfn.ftp.downloader.*")
class FTPFileDownloadRunnableTest {

    @Rule
    public TemporaryFolder rawFolder = new TemporaryFolder();

    @Rule
    public TemporaryFolder extractedFolder = new TemporaryFolder();

    FTPClient ftpClient;

    @BeforeEach
    void setUp() throws IOException {
        ftpClient = PowerMockito.mock(FTPClient.class);
        when(ftpClient.retrieveFile(Mockito.anyString(),Mockito.any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                Object[] args = invocation.getArguments();
                return true;
            }
        });
        when(ftpClient.getModificationTime(Mockito.any())).thenAnswer(new Answer<Object>() {
            @Override
            public Object answer(InvocationOnMock invocation) throws Throwable {
                return "20200212099009";
            }
        });
    }

    @Test
    void testFTPRun() throws Exception
    {
        rawFolder.create();
        extractedFolder.create();
        CountDownLatch latch = new CountDownLatch(1);
        String localFilePath = rawFolder.getRoot().getPath() + File.separator + "gene_info";
        String extractedFilePath = extractedFolder.getRoot().getPath() + File.separator + "gene_info";

        new Thread(new TestFTPFileDownloadRunnable("ftp://abc.com", "/gene/DATA", "gene_info", localFilePath, latch, extractedFilePath, false)).start();
        latch.await(10, TimeUnit.SECONDS);
        Assert.assertEquals(0, latch.getCount());

        File f1 = new File(localFilePath);
        Assert.assertTrue(f1.exists());
        Assert.assertTrue(f1.isDirectory());
        File[] files = f1.listFiles();
        Assert.assertEquals(1, files.length);
        Assert.assertTrue(files[0].getName().contains("gene_info"));
        Assert.assertTrue(files[0].getName().contains("20200212099009"));
    }

    @Test()
    void testFTPRunWithExtract() throws Exception
    {
        rawFolder.create();
        extractedFolder.create();
        CountDownLatch latch = new CountDownLatch(1);
        String localFilePath = rawFolder.getRoot().getPath() + File.separator + "gene_info";
        String extractedFilePath = extractedFolder.getRoot().getPath() + File.separator + "gene_info";

        new Thread(new TestFTPFileDownloadRunnable("ftp://abc.com", "/gene/DATA", "gene_info", localFilePath, latch, extractedFilePath, true)).start();
        latch.await(5, TimeUnit.SECONDS);

        Assert.assertEquals(1, latch.getCount());
    }

    class TestFTPFileDownloadRunnable extends FTPFileDownloadRunnable{

        public TestFTPFileDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, CountDownLatch latch, String extractedFileLocation, boolean extractFiles) {
            super(ftpServerURL, ftpFilePath, fileName, localFilePath, latch, extractedFileLocation, extractFiles);
        }

        @VisibleForTesting
        FTPClient getFtpClient(){
            return ftpClient;
        }
    }
}