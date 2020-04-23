package com.cscie599.gfn.service;

import com.cscie599.gfn.BaseTest;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.Resource;

import java.io.File;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author PulkitBhanot
 */
@SpringBootTest(properties = {
        "input.zippedFormat=false",
        "input.directory=./src/test/resources/test-data/extracted/",
        "output.directory.linesPerFile=20"
})
public class FileServiceTest extends BaseTest {

    @Autowired
    FileService fileService;

    @Value("file:${output.directory}${output.pubmed_meshterm_csv_gz.file}")
    private Resource outputResource;

    @Test
    public void testZip() throws Exception {
        fileService.splitAndZipFiles();
        assertTrue(outputResource.getFile().isDirectory());
        File[] filesCreated =  outputResource.getFile().listFiles();
        assertEquals(8, filesCreated.length);
        assertTrue(filesCreated[0].getName().contains("part"));
    }
}


