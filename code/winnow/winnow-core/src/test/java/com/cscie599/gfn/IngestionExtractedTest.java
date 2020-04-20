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
@SpringBootTest(properties = {
        "input.zippedFormat=false",
        "input.directory=./src/test/resources/test-data/extracted/"
})
public class IngestionExtractedTest extends BaseTest {

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
    public void testExtractedIngestion() throws Exception {
        ingestionService.ingestBaseData();
        ingestionService.ingestEnrichedData();
        assertEquals("Gene count", geneRepository.findAll().size(), 71);
        Gene gene = geneRepository.findByGeneId("814636").get(0);
        assertTrue(gene.getSymbol().equals("AT2G01080"));
        assertTrue(gene.getGeneId().equals("814636"));

        assertEquals("Gene Mesh count", geneMeshtermRepository.findAll().size(), 15);
        assertTrue(geneMeshtermRepository.findByGeneIdOrderByPValue("8655732").size() == 3);
        GeneMeshterm geneMeshterm = geneMeshtermRepository.findByGeneIdOrderByPValue("8655732").get(0);
        assertTrue(geneMeshterm.getGeneMeshtermPK().getGeneId().equals("8655732"));
        assertTrue(geneMeshterm.getGeneMeshtermPK().getMeshId().equals("D003063"));
        assertTrue(geneMeshterm.getPublicationCount() == 0);
        assertTrue(geneMeshterm.getPValue().equals(0.0095D));

        assertEquals("Meshterm category count", meshtermCategoryRepository.findAll().size(), 15);
        List<MeshtermCategory> allcategories = meshtermCategoryRepository.findByCategoryId("K");
        assertEquals(1, allcategories.size());
        assertEquals(allcategories.get(0).getName(), "Humanities");
        assertEquals(allcategories.get(0).getCategoryId(), "K");

        assertEquals("Meshterm count", meshtermRepository.findAll().size(), 70);
        List<Meshterm> meshtermList = meshtermRepository.findByMeshId("D009124");
        assertEquals(1, meshtermList.size());
        assertEquals("D009124", meshtermList.get(0).getMeshId());
        assertEquals("Muscle Proteins", meshtermList.get(0).getName());
        assertEquals(" Date create for meshTerm", MeshtermIngestor.DATE_FORMAT.format(new Date(915177600000L)), meshtermList.get(0).getDateCreated().toString());
        assertEquals(" Date revised for meshTerm", MeshtermIngestor.DATE_FORMAT.format(new Date(1120114800000L)), meshtermList.get(0).getDateRevised().toString());

        assertEquals("Meshterm tree count", meshtermTreeRepository.findAll().size(), 99);
        MeshtermTree meshtermTree = meshtermTreeRepository.findByTreeNodeId("187").get(0);
        assertEquals("D006422", meshtermTree.getMeshtermTreePK().getMeshId());
        assertEquals("D12.776.157.427.374", meshtermTree.getMeshtermTreePK().getTreeParentId());
        assertEquals("187", meshtermTree.getMeshtermTreePK().getTreeNodeId());
        assertEquals("Hemerythrin", meshtermTree.getMeshterm().getName());
        assertEquals("D006422", meshtermTree.getMeshterm().getMeshId());
        assertEquals(MeshtermIngestor.DATE_FORMAT.format(new Date(154080000000L)), meshtermTree.getMeshterm().getDateCreated().toString());
        assertEquals(MeshtermIngestor.DATE_FORMAT.format(new Date(1025679600000L)), meshtermTree.getMeshterm().getDateRevised().toString());

        assertEquals("Publication count", publicationRepository.findAll().size(), 9);
    }
}


