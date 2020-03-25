package com.cscie599.gfn;

import com.cscie599.gfn.ftp.downloader.FTPFileDownloadRunnable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 *
 * @author PulkitBhanot
 */
@SpringBootApplication
public class FTPFileDownloaderApp implements CommandLineRunner {


    protected static final Log logger = LogFactory.getLog(FTPFileDownloaderApp.class);

    private static String NCBI_FTP_SERVER_NAME = "ftp.ncbi.nlm.nih.gov";

    public static void main(String[] args) {
        SpringApplication.run(FTPFileDownloaderApp.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        CountDownLatch latch = new CountDownLatch(2);
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene_info", "/data/raw/gene_info", latch, "/data/extracted/")).start();
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene2go", "/data/raw/gene_info", latch, "/data/extracted/")).start();

        latch.await(30, TimeUnit.HOURS);
        if (latch.getCount() == 0) {
            logger.info("All files downloaded successfull, exiting now");
            System.exit(0);
        }
    }
}
