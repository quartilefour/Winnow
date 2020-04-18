package com.cscie599.gfn;

import com.cscie599.gfn.repository.*;
import com.cscie599.gfn.service.IngestionService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.Assert.assertEquals;

/**
 * @author PulkitBhanot
 */
@SpringBootTest(properties = {
        "input.zippedFormat=false",
        "input.directory=./src/test/resources/test-data/extracted/"
        })
public class IngestionTest extends BaseTest{

    @Autowired
    IngestionService ingestionService;

    @Autowired
    GeneRepository geneRepository;

    @Autowired
    GeneMeshtermRepository geneMeshtermRepository;

    @Autowired
    MeshtermCategoryRepository meshtermCategoryRepository;

    @Autowired
    MeshtermRepository meshtermRepository;

    @Autowired
    MeshtermTreeRepository meshtermTreeRepository;

    @Autowired
    PublicationRepository publicationRepository;

    @Test
    public void testRawIngestion() throws Exception {
        ingestionService.startIngestion();
        assertEquals("Gene count",geneRepository.findAll().size(),26);
        assertEquals("Gene Mesh count",geneMeshtermRepository.findAll().size(), 3);
        assertEquals("Meshterm category count",meshtermCategoryRepository.findAll().size(),15);
        assertEquals("Meshterm count",meshtermRepository.findAll().size(), 70);
        assertEquals("Meshterm tree count",meshtermTreeRepository.findAll().size(), 99);
        assertEquals("Publication count",publicationRepository.findAll().size(), 9);
    }

    @Test
    public void testExtractedIngestion() throws Exception {
        ingestionService.startIngestion();
    }

}


