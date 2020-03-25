package com.cscie599.gfn.ftp.downloader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author PulkitBhanot
 */
public class FTPFileDownloadRunnable  implements Runnable {

    protected static final Log logger = LogFactory.getLog(FTPFileDownloadRunnable.class);
    // URL of the server from where the file has to be downloaded
    private final String ftpServerURL;
    // Path on the ftp server for the location of the file
    private final String ftpFilePath;
    // Exact filename to be downloaded
    private final String fileName;
    // Path where the raw file from the server is going to be downloaded
    private final String localFilePath;
    // username for the FTP server
    private final String username;
    // password to connect to the FTP server
    private final String password;
    // Location where the unzipped file will be stored
    private final String extractedFileLocation;
    // latch to be marked as processed successfully
    private final CountDownLatch latch;
    private final static String FTP_USERNAME = "anonymous";
    private final static String FTP_USERPASS = "";
    private final static String FTP_FILEEXTENSION = ".gz";
    //date format to be appended to the file
    private final static SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDhhmmss");
    byte[] buffer = new byte[1024];

    public FTPFileDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, CountDownLatch latch, String extractedFileLocation) {
        this(ftpServerURL, ftpFilePath, fileName, localFilePath, FTP_USERNAME, FTP_USERPASS, latch, extractedFileLocation);
    }

    public FTPFileDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, String username, String password, CountDownLatch latch, String extractedFileLocation) {
        this.ftpServerURL = ftpServerURL;
        this.ftpFilePath = ftpFilePath;
        this.fileName = fileName;
        this.localFilePath = localFilePath;
        this.username = username;
        this.password = password;
        this.latch = latch;
        this.extractedFileLocation = extractedFileLocation;
    }

    @Override
    public void run() {
        try {
            FTPClient ftpClient = null;
            String time = null;
            String filePath = this.ftpFilePath + File.separator + this.fileName + FTP_FILEEXTENSION;
            int iteration = 0;
            do {
                ftpClient = new FTPClient();
                try {
                    logger.info("Running for iteration " + iteration);
                    iteration++;
                    ftpClient.connect(ftpServerURL);
                    ftpClient.login(this.username, this.password);
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    ftpClient.setBufferSize(33554432); // 32mb
                    logger.info("File path on the ftp server" + filePath);
                    time = ftpClient.getModificationTime(filePath);
                } catch (IOException e) {
                    logger.warn("Unable to connect to ftpserver, will retry again.");
                }
            } while ((time == null || time.isEmpty()) && iteration < 5);
            if (time == null || time.isEmpty()) {
                logger.error("Unable to process the file " + fileName);
                latch.countDown();
            }
            sdf.parse(time);
            Path newDirPath = Paths.get(this.localFilePath);
            if (!Files.exists(newDirPath)) {
                Files.createDirectories(newDirPath);
            }
            String localFilePath = this.localFilePath + File.separator + this.fileName + "_" + time + FTP_FILEEXTENSION;
            logger.info("Local File path on the local server " + localFilePath);

            File outputFile = new File(localFilePath);
            if (outputFile.exists()) {
                logger.info("file already downloaded");
            } else {
                logger.info("Downloading file" + outputFile.getName());
                FileOutputStream fos = new FileOutputStream(outputFile);
                ftpClient.retrieveFile(filePath, fos);
                fos.flush();
                fos.close();
                logger.info("File Download complete " + outputFile.getName() + " starting with extraction of the file now");
                GZIPInputStream gzis =
                        new GZIPInputStream(new FileInputStream(outputFile));

                FileOutputStream out =
                        new FileOutputStream(extractedFileLocation + fileName);
                int len;
                while ((len = gzis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                gzis.close();
                out.close();
                logger.info("Unzipping of Downloaded file done " + fileName);
            }
            latch.countDown();
            ftpClient.disconnect();
        } catch (Exception e) {
            logger.error("Unable to successfully complete the processing of file " + fileName, e);
        }
    }
}

