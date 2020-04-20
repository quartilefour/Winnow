package com.cscie599.gfn;

import com.cscie599.gfn.entities.*;
import com.cscie599.gfn.ingestor.basedata.MeshtermIngestor;
import com.cscie599.gfn.repository.*;
import com.cscie599.gfn.service.IngestionService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * @author PulkitBhanot
 */
@SpringBootTest
public class IngestionTest extends BaseTest {

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

    @BeforeClass
    public static void onlyOnce() {
        System.setProperty("user.timezone", "GMT");
    }

    @Test
    public void testRawIngestion() throws Exception {
        ingestionService.ingestBaseData();
        ingestionService.ingestEnrichedData();
        assertEquals("Gene count", geneRepository.findAll().size(), 72);
        Gene gene = geneRepository.findByGeneId("1246503").get(0);
        assertTrue(gene.getSymbol().equals("leuB"));
        assertTrue(gene.getGeneId().equals("1246503"));

        assertEquals("Gene Mesh count", geneMeshtermRepository.findAll().size(), 15);
        assertTrue(geneMeshtermRepository.findByGeneIdOrderByPValue("5692769").size() == 3);
        GeneMeshterm geneMeshterm = geneMeshtermRepository.findByGeneIdOrderByPValue("5692769").get(0);
        assertTrue(geneMeshterm.getGeneMeshtermPK().getGeneId().equals("5692769"));
        assertTrue(geneMeshterm.getGeneMeshtermPK().getMeshId().equals("D000818"));
        assertTrue(geneMeshterm.getPublicationCount() == 12);
        assertTrue(geneMeshterm.getPValue().equals(0.012D));

        assertEquals("Meshterm category count", meshtermCategoryRepository.findAll().size(), 16);

        List<MeshtermCategory> allcategories = meshtermCategoryRepository.findByCategoryId("C");
        assertEquals(1, allcategories.size());
        assertEquals(allcategories.get(0).getName(), "Diseases");
        assertEquals(allcategories.get(0).getCategoryId(), "C");

        assertEquals("Meshterm count", meshtermRepository.findAll().size(), 70);

        List<Meshterm> meshtermList = meshtermRepository.findByMeshId("D005561");
        assertEquals(1, meshtermList.size());
        assertEquals("D005561", meshtermList.get(0).getMeshId());
        assertEquals("Formates", meshtermList.get(0).getName());
        assertEquals(" Date create for meshTerm", MeshtermIngestor.DATE_FORMAT.format(new Date(915177600000L)), meshtermList.get(0).getDateCreated().toString());
        assertEquals(" Date revised for meshTerm", MeshtermIngestor.DATE_FORMAT.format(new Date(1341298800000L)), meshtermList.get(0).getDateRevised().toString());

        assertEquals("Meshterm tree count", meshtermTreeRepository.findAll().size(), 99);
        MeshtermTree meshtermTree = meshtermTreeRepository.findByTreeNodeId("260").get(0);
        assertEquals("D003201", meshtermTree.getMeshtermTreePK().getMeshId());
        assertEquals("L01.224.230", meshtermTree.getMeshtermTreePK().getTreeParentId());
        assertEquals("260", meshtermTree.getMeshtermTreePK().getTreeNodeId());
        assertEquals("Computers", meshtermTree.getMeshterm().getName());
        assertEquals("D003201", meshtermTree.getMeshterm().getMeshId());
        assertEquals(MeshtermIngestor.DATE_FORMAT.format(new Date(915177600000L)), meshtermTree.getMeshterm().getDateCreated().toString());
        assertEquals(MeshtermIngestor.DATE_FORMAT.format(new Date(1520838000000L)), meshtermTree.getMeshterm().getDateRevised().toString());

        assertEquals("Publication count", publicationRepository.findAll().size(), 9);
    }

}


