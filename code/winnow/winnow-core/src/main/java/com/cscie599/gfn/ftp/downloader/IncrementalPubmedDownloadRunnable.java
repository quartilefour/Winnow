package com.cscie599.gfn.ftp.downloader;

import org.apache.commons.io.FileUtils;
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
import java.util.BitSet;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;

/**
 * @author PulkitBhanot
 */
public class IncrementalPubmedDownloadRunnable extends BasePubmedDownloadRunnable implements Runnable {

    protected static final Log logger = LogFactory.getLog(IncrementalPubmedDownloadRunnable.class);

    private static final int FIRST_FILE_INDEX = 1016;
    //As of writing the max update file number is only 1140
    private static final int LAST_FILE_INDEX = 2048;
    byte[] buffer = new byte[1024];
    private final BitSet filesProcessed;

    public IncrementalPubmedDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, CountDownLatch latch, String extractedFileLocation, boolean extractFiles) {
        super(ftpServerURL, ftpFilePath, fileName, localFilePath, FTP_USERNAME, FTP_USERPASS, latch, extractedFileLocation, extractFiles);
        filesProcessed = new BitSet(LAST_FILE_INDEX + 1);
        filesProcessed.flip(0, FIRST_FILE_INDEX);
    }

    @Override
    public void run() {
        try {
            String rawFolderLocation = this.localFilePath;
            Path newDirPath = Paths.get(rawFolderLocation);
            if (!Files.exists(newDirPath)) {
                Files.createDirectories(newDirPath);
            }

            String extractedFolderLocation = this.extractedFileLocation + "pubmed/";
            newDirPath = Paths.get(extractedFolderLocation);
            if (!Files.exists(newDirPath)) {
                Files.createDirectories(newDirPath);
            }
            //ftp://ftp.ncbi.nlm.nih.gov/pubmed/updatefiles/pubmed20n1016.xml.gz
            Iterator<File> it = FileUtils.iterateFiles(new File(localFilePath), null, false);
            while (it.hasNext()) {
                File file = (File) it.next();
                logger.info("File Name found" + file.getName());
                String fileNameWithOutExt = file.getName().replaceAll(".xml.gz", "");
                String lastFourDigits = fileNameWithOutExt.substring(fileNameWithOutExt.length() - 4);
                int index = Integer.parseInt(lastFourDigits);
                if (index > FIRST_FILE_INDEX && index <= LAST_FILE_INDEX)
                    filesProcessed.flip(index);
                else {
                    logger.warn("Not counting baseline files" + fileNameWithOutExt);
                }
                logger.info("File name" + fileNameWithOutExt);
            }
            int index = filesProcessed.nextClearBit(FIRST_FILE_INDEX);
            int retry_count = 0;
            while (index <= LAST_FILE_INDEX && retry_count < 5) {
                logger.info("Starting from index" + index);
                FTPClient ftpClient = new FTPClient();
                String filePath = this.ftpFilePath + File.separator + "pubmed20n" + String.format("%04d", index) + FTP_FILEEXTENSION;

                try {
                    ftpClient.connect(ftpServerURL);
                    ftpClient.login(this.username, this.password);
                    ftpClient.enterLocalPassiveMode();
                    ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                    ftpClient.setBufferSize(1671168); // 16mb
                    logger.info("File path on the ftp server " + filePath);

                    String localFilePath = this.localFilePath + File.separator + "pubmed20n" + String.format("%04d", index) + FTP_FILEEXTENSION;
                    File outputFile = new File(localFilePath);

                    logger.info("Downloading file" + outputFile.getName());
                    FileOutputStream fos = new FileOutputStream(outputFile);
                    ftpClient.retrieveFile(filePath, fos);
                    fos.flush();
                    fos.close();
                    logger.info("Local File path on the local server " + localFilePath);
                    GZIPInputStream gzis = new GZIPInputStream(new FileInputStream(outputFile));

                    FileOutputStream out =
                            new FileOutputStream(extractedFolderLocation + File.separator + "pubmed20n" + String.format("%04d", index) + EXTRACTED_FILEEXTENSION);
                    int len;
                    while ((len = gzis.read(buffer)) > 0) {
                        out.write(buffer, 0, len);
                    }
                    gzis.close();
                    out.close();
                    logger.info("Unzipping of Downloaded file done " + fileName);
                    filesProcessed.flip(index);
                    index = filesProcessed.nextClearBit(FIRST_FILE_INDEX);
                    retry_count = 0;
                } catch (IOException e) {
                    retry_count++;
                    logger.warn("Unable to connect to ftpserver, will retry again.", e);
                }
            }
            logger.info("Marking job as finished successfully");
        } catch (Exception ex) {
            logger.error("Unable to complete processing of files " + fileName);
        } finally {
            latch.countDown();
        }
    }
}

