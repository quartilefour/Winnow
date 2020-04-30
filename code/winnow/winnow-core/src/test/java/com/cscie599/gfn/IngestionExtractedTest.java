package com.cscie599.gfn;

import com.cscie599.gfn.entities.*;
import com.cscie599.gfn.ingestor.basedata.MeshtermIngestor;
import com.cscie599.gfn.repository.*;
import com.cscie599.gfn.service.IngestionService;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.SimpleDateFormat;
import java.util.Arrays;
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

    private final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd");

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
        assertEquals("Gene count", geneRepository.findAll().size(), 72);
        Gene gene = geneRepository.findByGeneId("814636").get(0);
        assertTrue(gene.getSymbol().equals("AT2G01080"));
        assertTrue(gene.getGeneId().equals("814636"));

        assertEquals("Gene Mesh count", geneMeshtermRepository.findAll().size(), 127);
        assertTrue(geneMeshtermRepository.findByGeneIdOrderByPValue("8655732").size() == 3);
        GeneMeshterm geneMeshterm = geneMeshtermRepository.findByGeneIdOrderByPValue("8655732").get(0);
        assertTrue(geneMeshterm.getGeneMeshtermPK().getGeneId().equals("8655732"));
        assertTrue(geneMeshterm.getGeneMeshtermPK().getMeshId().equals("D003063"));
        assertTrue(geneMeshterm.getPublicationCount() == 0);
        assertTrue(geneMeshterm.getPValue().equals(0.0095D));

        assertEquals("Meshterm category count", meshtermCategoryRepository.findAll().size(), 16);
        List<MeshtermCategory> allcategories = meshtermCategoryRepository.findByCategoryId("K");
        assertEquals(1, allcategories.size());
        assertEquals(allcategories.get(0).getName(), "Humanities");
        assertEquals(allcategories.get(0).getCategoryId(), "K");

        assertEquals("Meshterm count", meshtermRepository.findAll().size(), 70);
        List<Meshterm> meshtermList = meshtermRepository.findByMeshId("D009124");
        assertEquals(1, meshtermList.size());
        assertEquals("D009124", meshtermList.get(0).getMeshId());
        assertEquals("Muscle Proteins", meshtermList.get(0).getName());
        assertEquals(" Date create for meshTerm", DATE_FORMAT.format(new Date(915177600000L)), meshtermList.get(0).getDateCreated().toString());
        assertEquals(" Date revised for meshTerm", DATE_FORMAT.format(new Date(1120114800000L)), meshtermList.get(0).getDateRevised().toString());

        assertEquals("Meshterm tree count", meshtermTreeRepository.findAll().size(), 99);
        MeshtermTree meshtermTree = meshtermTreeRepository.findByTreeNodeId("187").get(0);
        assertEquals("D006422", meshtermTree.getMeshtermTreePK().getMeshId());
        assertEquals("D12.776.157.427.374", meshtermTree.getMeshtermTreePK().getTreeParentId());
        assertEquals("187", meshtermTree.getMeshtermTreePK().getTreeNodeId());
        assertEquals("Hemerythrin", meshtermTree.getMeshterm().getName());
        assertEquals("D006422", meshtermTree.getMeshterm().getMeshId());
        assertEquals(DATE_FORMAT.format(new Date(154080000000L)), meshtermTree.getMeshterm().getDateCreated().toString());
        assertEquals(DATE_FORMAT.format(new Date(1025679600000L)), meshtermTree.getMeshterm().getDateRevised().toString());

        List<Integer> taxIdArray = Arrays.asList(new Integer(9606));

        // publication tests, positive tests
        assertEquals("Publication count", publicationRepository.findAll().size(), 9);
        assertEquals("Publications for Gene-Meshterm", publicationRepository.findByGeneIdAndMeshId("5692769", "D000313",taxIdArray).size(), 1);
        assertEquals("Publications for Gene-Meshterm", publicationRepository.findByGeneIdAndMeshId("8655736", "D000818",taxIdArray).size(), 2);
        assertEquals("Publications for Gene-Meshterm", publicationRepository.findByGeneIdAndMeshId("8655733", "D000818",taxIdArray).size(), 2);
        assertTrue(publicationRepository.findByGeneIdAndMeshId("5692769", "D000313",taxIdArray).get(0).getPublicationId().equals("7"));
        assertTrue(publicationRepository.findByGeneIdAndMeshId("8655733", "D006801",taxIdArray).get(0).getPublicationId().equals("3"));

        // negative test: valid gene_id, not ingested for publications
        assertTrue(publicationRepository.findByGeneIdAndMeshId("1", "D000313",taxIdArray).isEmpty());

        // negative test: invalid gene_id, not ingested
        assertTrue(publicationRepository.findByGeneIdAndMeshId("A", "D000313",taxIdArray).isEmpty());

        // negative test: valid mesh_id, not ingested for publications
        assertTrue(publicationRepository.findByGeneIdAndMeshId("1246509", "D000001",taxIdArray).isEmpty());

        // negative test: invalid mesh_id, not ingested
        assertTrue(publicationRepository.findByGeneIdAndMeshId("1246509", "D000000",taxIdArray).isEmpty());

        // no genes were ingested for publication id == 1, so no queries to 'findByGeneIdAndMeshId' should contain PMID '1'
        List<Meshterm> allMesh = meshtermRepository.findAll();
        List<Gene> allGene = geneRepository.findAll();
        for (Gene g : allGene) {
            for (Meshterm m : allMesh) {
                List<Publication> combinationPublication = publicationRepository.findByGeneIdAndMeshId(g.getGeneId(), m.getMeshId(),taxIdArray);
                for (Publication p : combinationPublication) {
                    assertTrue(! p.getPublicationId().equals("1"));
                }
            }
        }

        List<Publication> publications = publicationRepository.findAll();
        for (Publication p : publications) {
            assertTrue(! p.getPublicationId().equals("9")); // negative test

            // positive tests: test title's ingested properly
            if (p.getPublicationId().equals("1")) {
                assertTrue(p.getTitle().equals("Formate assay in body fluids: application in methanol poisoning."));
            }
            else if (p.getPublicationId().equals("2")) {
                assertTrue(p.getTitle().equals("Delineation of the intimate details of the backbone conformation of pyridine nucleotide coenzymes in aqueous solution."));
            }
            else if (p.getPublicationId().equals("8")) {
                assertTrue(p.getTitle().equals("Comparison between procaine and isocarboxazid metabolism in vitro by a liver microsomal amidase-esterase."));
            }
        }
    }
}


