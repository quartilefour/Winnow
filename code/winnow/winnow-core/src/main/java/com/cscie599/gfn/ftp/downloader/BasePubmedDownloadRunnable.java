package com.cscie599.gfn.ftp.downloader;

import org.apache.commons.net.ftp.FTPClient;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

/**
 * @author PulkitBhanot
 */
public abstract class BasePubmedDownloadRunnable implements Runnable {

    protected final String ftpServerURL;
    protected final String ftpFilePath;
    protected final String localFilePath;
    protected final String fileName;
    protected final String username;
    protected final String password;
    protected final CountDownLatch latch;
    // Location where the unzipped file will be stored
    protected final String extractedFileLocation;
    protected final static String FTP_USERNAME = "anonymous";
    protected final static String FTP_USERPASS = "";
    protected final static String FTP_FILEEXTENSION = ".xml.gz";
    protected final static String EXTRACTED_FILEEXTENSION = ".xml";

    protected final boolean extractContent;
    public BasePubmedDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, String username, String password, CountDownLatch latch, String extractedFileLocation, boolean extractFiles) {
        this.ftpServerURL = ftpServerURL;
        this.ftpFilePath = ftpFilePath;
        this.fileName = fileName;
        this.localFilePath = localFilePath;
        this.username = username;
        this.password = password;
        this.latch = latch;
        this.extractedFileLocation = extractedFileLocation;
        this.extractContent = extractFiles;
    }

    protected FTPClient getFtpClient() throws IOException {
        FTPClient ftpClient = new FTPClient();

        ftpClient.connect(ftpServerURL);
        ftpClient.login(this.username, this.password);
        ftpClient.enterLocalPassiveMode();
        ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
        ftpClient.setBufferSize(1671168); // 16mb
        return ftpClient;
    }
}
