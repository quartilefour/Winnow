package com.cscie599.gfn.service;


import com.cscie599.gfn.ingestor.basedata.PubmedXMLIngester;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.zip.GZIPOutputStream;

/**
 * An implementation of {@href FileService}. This is used to compress the publication_meshterms file that is produced
 * as part of base ingestion. That file has ~300M rows. This utility service breaks that file into smaller chunks and zips the resulting files.
 *
 * @author PulkitBhanot
 */
@Service
public class FileServiceImpl implements FileService {

    protected static final Log logger = LogFactory.getLog(FileServiceImpl.class);

    // Output file location where the publication meshterm association needs to be stored.
    @Value("file:${output.directory}${output.pubmed_meshterm_csv.file}")
    private Resource inputResource;

    // Number of lines per file.
    @Value("${output.directory.linesPerFile:40000000}")
    private int linesPerFile;

    // Output file location where the publication meshterm association needs to be stored.
    @Value("file:${output.directory}${output.pubmed_meshterm_csv_gz.file}")
    private Resource outputResource;

    byte[] newLineBytes = new String("\r\n").getBytes();

    @Override
    public void splitAndZipFiles() throws IOException {
        int lineCount = 0;
        AtomicInteger index = new AtomicInteger(0);
        GZIPOutputStream gzipOS = getFileName(index);
        try {
            Scanner scanner = new Scanner(inputResource.getFile());
            while (scanner.hasNextLine()) {
                gzipOS.write(scanner.nextLine().getBytes());
                gzipOS.write(newLineBytes);
                lineCount++;
                if (lineCount % linesPerFile == 0) {
                    gzipOS.flush();
                    gzipOS.close();
                    gzipOS = getFileName(index);
                }
            }
            scanner.close();
            gzipOS.flush();
            gzipOS.close();
        } catch (Exception e) {
            logger.error("Error in splitAndZipFiles ", e);
        }
    }

    private GZIPOutputStream getFileName(AtomicInteger index) throws IOException {
        String fileName = outputResource.getFile().toString() + File.separator + "part" + index.getAndIncrement() + ".gz";
        FileOutputStream fos = new FileOutputStream(fileName);
        return new GZIPOutputStream(new BufferedOutputStream(fos));
    }

}