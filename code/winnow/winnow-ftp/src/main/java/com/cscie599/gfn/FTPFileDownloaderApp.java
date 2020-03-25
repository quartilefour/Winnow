package com.cscie599.gfn;

import com.cscie599.gfn.ftp.downloader.BaselinePubmedDownloadRunnable;
import com.cscie599.gfn.ftp.downloader.FTPFileDownloadRunnable;
import com.cscie599.gfn.ftp.downloader.HTTPGeneOntologyDownloadRunnable;
import com.cscie599.gfn.ftp.downloader.IncrementalPubmedDownloadRunnable;
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
    private static String NLMPUBS_FTP_SERVER_NAME = "nlmpubs.nlm.nih.gov";

    public static void main(String[] args) {
        SpringApplication.run(FTPFileDownloaderApp.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        CountDownLatch latch = new CountDownLatch(8);
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene_info", "/data/raw/gene_info", latch, "/data/extracted/gene_info/")).start();
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene2go", "/data/raw/gene2go", latch, "/data/extracted/gene2go/")).start();
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene2pubmed", "/data/raw/gene2pubmed", latch, "/data/extracted/gene2pubmed/")).start();
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene_group", "/data/raw/gene_group", latch, "/data/extracted/gene_group/")).start();
        new Thread(new FTPFileDownloadRunnable(NLMPUBS_FTP_SERVER_NAME, "/online/mesh/MESH_FILES/xmlmesh", "desc2020", "/data/raw/xmlmesh", latch, "/data/extracted/xmlmesh/")).start();
        new Thread(new HTTPGeneOntologyDownloadRunnable("gene_ontology", latch, "/data/extracted/")).start();
        new Thread(new BaselinePubmedDownloadRunnable(NCBI_FTP_SERVER_NAME,"/pubmed/baseline","pubmed", "/data/raw/pubmed", latch, "/data/extracted/")).start();
        new Thread(new IncrementalPubmedDownloadRunnable(NCBI_FTP_SERVER_NAME,"/pubmed/updatefiles","pubmed", "/data/raw/pubmed", latch, "/data/extracted/")).start();

        latch.await(60, TimeUnit.HOURS);
        if (latch.getCount() == 0) {
            logger.info("All files downloaded successfully, exiting now");
            System.exit(0);
        }
    }
}
