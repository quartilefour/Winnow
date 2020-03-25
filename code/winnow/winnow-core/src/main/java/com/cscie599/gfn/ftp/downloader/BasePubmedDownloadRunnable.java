package com.cscie599.gfn.ftp.downloader;

import java.util.concurrent.CountDownLatch;

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

    public BasePubmedDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, String username, String password, CountDownLatch latch, String extractedFileLocation) {
        this.ftpServerURL = ftpServerURL;
        this.ftpFilePath = ftpFilePath;
        this.fileName = fileName;
        this.localFilePath = localFilePath;
        this.username = username;
        this.password = password;
        this.latch = latch;
        this.extractedFileLocation = extractedFileLocation;
    }
}
