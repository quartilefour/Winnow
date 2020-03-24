package com.cscie599.gfn.ftp.downloader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;

/**
 *
 * @author PulkitBhanot
 */
public class IncrementalPubmedDownloadRunnable  implements Runnable{

    protected static final Log logger = LogFactory.getLog(IncrementalPubmedDownloadRunnable.class);

    private final String ftpServerURL;
    private final String ftpFilePath;
    private final String localFilePath;
    private final String fileName;
    private final String username;
    private final String password;
    private final CountDownLatch latch;
    private final static String FTP_USERNAME = "anonymous";
    private final static String FTP_USERPASS = "";
    private final static String FTP_FILEEXTENSION = ".gz";
    private final static SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDhhmmss");
    byte[] buffer = new byte[1024];
    private static final int FIRST_FILE_INDEX = 1015;

    public IncrementalPubmedDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, CountDownLatch latch) {
        this(ftpServerURL, ftpFilePath, fileName, localFilePath, FTP_USERNAME, FTP_USERPASS, latch);
    }

    public IncrementalPubmedDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, String username, String password, CountDownLatch latch) {
        this.ftpServerURL = ftpServerURL;
        this.ftpFilePath = ftpFilePath;
        this.fileName = fileName;
        this.localFilePath = localFilePath;
        this.username = username;
        this.password = password;
        this.latch = latch;
    }
    @Override
    public void run() {

    }
}

