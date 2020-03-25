package com.cscie599.gfn.ftp.downloader;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.commons.net.ftp.FTPClient;
import org.apache.commons.io.FileUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.BitSet;
import java.util.Iterator;
import java.util.concurrent.CountDownLatch;
import java.util.zip.GZIPInputStream;

/**
 *
 * @author PulkitBhanot
 */
public class BaselinePubmedDownloadRunnable implements Runnable{

    protected static final Log logger = LogFactory.getLog(BaselinePubmedDownloadRunnable.class);

    private final String ftpServerURL;
    private final String ftpFilePath;
    private final String localFilePath;
    private final String fileName;
    private final String username;
    private final String password;
    private final CountDownLatch latch;
    private final static String FTP_USERNAME = "anonymous";
    private final static String FTP_USERPASS = "";
    private final static String FTP_FILEEXTENSION = ".xml.gz";
    private static final int LAST_FILE_INDEX = 1015;
    private final static SimpleDateFormat sdf = new SimpleDateFormat("YYYYMMDDhhmmss");
    byte[] buffer = new byte[1024];
    BitSet filesProcessed = new BitSet(LAST_FILE_INDEX+1);
    public BaselinePubmedDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, CountDownLatch latch) {
        this(ftpServerURL, ftpFilePath, fileName, localFilePath, FTP_USERNAME, FTP_USERPASS, latch);
    }

    public BaselinePubmedDownloadRunnable(String ftpServerURL, String ftpFilePath, String fileName, String localFilePath, String username, String password, CountDownLatch latch) {
        this.ftpServerURL = ftpServerURL;
        this.ftpFilePath = ftpFilePath;
        this.fileName = fileName;
        this.localFilePath = localFilePath;
        this.username = username;
        this.password = password;
        this.latch = latch;
        filesProcessed.flip(0);
    }

    @Override
    public void run() {

        //ftp://ftp.ncbi.nlm.nih.gov/pubmed/updatefiles/pubmed20n1016.xml.gz
        Iterator<File> it = FileUtils.iterateFiles(new File(localFilePath),null,false);
        while(it.hasNext()){
            File file = (File) it.next();
            logger.info("File Name found"+file.getName());
            String fileNameWithOutExt = file.getName().replaceAll(".xml.gz","");
            String lastFourDigits = fileNameWithOutExt.substring(fileNameWithOutExt.length() - 4);
            int index = Integer.parseInt(lastFourDigits);
            if(index<=LAST_FILE_INDEX)
                filesProcessed.flip(index);
            else{
                logger.warn("Not counting incremental files"+fileNameWithOutExt);
            }
            System.out.println(fileNameWithOutExt);
        }
        int index = filesProcessed.nextClearBit(0);
        do{
            logger.info("Starting from index"+index);
            FTPClient ftpClient = new FTPClient();
            String filePath = this.ftpFilePath + File.separator + "pubmed20n"+ String.format ("%04d", index) + FTP_FILEEXTENSION;

            try {
                ftpClient.connect(ftpServerURL);
                ftpClient.login(this.username, this.password);
                ftpClient.enterLocalPassiveMode();
                ftpClient.setFileType(FTPClient.BINARY_FILE_TYPE);
                ftpClient.setBufferSize(1671168); // 16mb
                logger.info("File path on the ftp server" + filePath);

                String localFilePath = this.localFilePath + File.separator + "pubmed20n"+ String.format ("%04d", index) + FTP_FILEEXTENSION;
                File outputFile = new File(localFilePath);

                logger.info("Downloading file" + outputFile.getName());
                FileOutputStream fos = new FileOutputStream(outputFile);
                ftpClient.retrieveFile(filePath, fos);
                fos.flush();
                fos.close();
                logger.info("Local File path on the local server" + localFilePath);
                GZIPInputStream gzis =
                        new GZIPInputStream(new FileInputStream(outputFile));

                FileOutputStream out =
                        new FileOutputStream("/tmp/data/extracted/pubmed/" + fileName);
                int len;
                while ((len = gzis.read(buffer)) > 0) {
                    out.write(buffer, 0, len);
                }
                gzis.close();
                out.close();
                logger.info("Unzipping of Downloaded file done" + fileName);
                filesProcessed.flip(index);
                index = filesProcessed.nextClearBit(0);
            } catch (IOException e) {
                logger.warn("Unable to connect to ftpserver, will retry again.");
            }
        }while(index<=LAST_FILE_INDEX);
    }
}