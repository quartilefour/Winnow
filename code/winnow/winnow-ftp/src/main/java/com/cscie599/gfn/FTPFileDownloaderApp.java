package com.cscie599.gfn;

import com.cscie599.gfn.ftp.downloader.BaselinePubmedDownloadRunnable;
import com.cscie599.gfn.ftp.downloader.FTPFileDownloadRunnable;
import com.cscie599.gfn.ftp.downloader.HTTPGeneOntologyDownloadRunnable;
import com.cscie599.gfn.ftp.downloader.IncrementalPubmedDownloadRunnable;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

/**
 * @author PulkitBhanot
 */
@SpringBootApplication
public class FTPFileDownloaderApp implements CommandLineRunner {


    protected static final Log logger = LogFactory.getLog(FTPFileDownloaderApp.class);

    private static String NCBI_FTP_SERVER_NAME = "ftp.ncbi.nlm.nih.gov";
    private static String NLMPUBS_FTP_SERVER_NAME = "nlmpubs.nlm.nih.gov";

    @Value("${ftp.download.extractFiles}")
    private boolean extractContent;

    public static void main(String[] args) {
        SpringApplication.run(FTPFileDownloaderApp.class, args);
    }


    @Override
    public void run(String... args) throws Exception {
        logger.info("Running FTP downloader app with extractContent" + extractContent);
        CountDownLatch latch = new CountDownLatch(9);
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene_info", "/data/raw/gene_info", latch, "/data/extracted/gene_info/", extractContent)).start();
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene2go", "/data/raw/gene2go", latch, "/data/extracted/gene2go/", extractContent)).start();
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene2pubmed", "/data/raw/gene2pubmed", latch, "/data/extracted/gene2pubmed/", extractContent)).start();
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene_group", "/data/raw/gene_group", latch, "/data/extracted/gene_group/", extractContent)).start();
        // File structure for gene_orthologs is exactly same to to that of gene_group. Also given that we support ingestion of all files in a given directory. We just need to download this file in that directory.
        new Thread(new FTPFileDownloadRunnable(NCBI_FTP_SERVER_NAME, "/gene/DATA", "gene_orthologs", "/data/raw/gene_group", latch, "/data/extracted/gene_group/", extractContent)).start();
        new Thread(new FTPFileDownloadRunnable(NLMPUBS_FTP_SERVER_NAME, "/online/mesh/MESH_FILES/xmlmesh", "desc2020", "/data/raw/xmlmesh", latch, "/data/extracted/xmlmesh/", extractContent)).start();
        new Thread(new HTTPGeneOntologyDownloadRunnable("gene_ontology", latch, "/data/extracted/", "/data/raw/", extractContent)).start();
        new Thread(new BaselinePubmedDownloadRunnable(NCBI_FTP_SERVER_NAME, "/pubmed/baseline", "pubmed", "/data/raw/pubmed", latch, "/data/extracted/", extractContent)).start();
        new Thread(new IncrementalPubmedDownloadRunnable(NCBI_FTP_SERVER_NAME, "/pubmed/updatefiles", "pubmed", "/data/raw/pubmed", latch, "/data/extracted/", extractContent)).start();

        latch.await(30, TimeUnit.HOURS);
        if (latch.getCount() == 0) {
            logger.info("All files downloaded successfully, exiting now");
            System.exit(0);
        } else {
            logger.warn("All files not downloaded, exiting now");
            System.exit(1);
        }
    }
}
