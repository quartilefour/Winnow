package com.cscie599.gfn;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.Assert.assertEquals;

/**
 * @author PulkitBhanot
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = {
                "search.result.limit=3"
        }
)
public class SearchControllerPaginationTest extends SearchControllerBaseTest {

    private static boolean isSetUp = false;

    @BeforeClass
    public static void runBeforeAllTestMethods() throws JSONException {
        initialize();
    }

    @Before
    public void setUp() throws Exception {
        if (!(isSetUp)) {
            HttpEntity<String> registerRequest = new HttpEntity<>(userJsonObject.toString(), userHeaders);
            testRestTemplate.postForEntity(createURLWithPort("/api/registration"), registerRequest, String.class);
            HttpEntity<String> loginRequest = new HttpEntity<>(loginJsonObject.toString(), loginHeaders);
            ResponseEntity<String> loginResponse = testRestTemplate.postForEntity(createURLWithPort("/api/login"), loginRequest, String.class);
            searchHeaders.set("Authorization", loginResponse.getHeaders().getFirst("Authorization"));
            jobLauncherController.handle();
            fileHeaders.set("Authorization", loginResponse.getHeaders().getFirst("Authorization"));
        }
        isSetUp = true;
    }

    @Test
    public void testExecuteSearchWithMeshIds() throws JSONException {
        logger.info("testExecuteSearchWithMeshIds");
        JSONObject searchWithSearchQueryJsonObject = new JSONObject();
        searchWithSearchQueryJsonObject.put("searchQuery", meshIdsSearchQueryJsonObject);
        HttpEntity<String> request = new HttpEntity<>(searchWithSearchQueryJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        String expectedResponse = "{\"searchQuery\":{\"symbol\":[],\"geneId\":[],\"meshTreeId\":[],\"name\":[],\"description\":[],\"meshId\":[\"D003063\"]},\"results\":[{\"index\":0,\"geneId\":\"8655732\",\"description\":\"3-isopropylmalate dehydratase large subunit; leucine biosinthesis\",\"symbol\":\"leuC\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":0,\"pvalue\":0.0095},{\"index\":1,\"geneId\":\"5692769\",\"description\":\"Record to support submission of GeneRIFs for a gene not in Gene (Azotirhizobium caulinodans.  Use when strain, subtype, isolate, etc. is unspecified, or when different from all specified ones in Gene.).\",\"symbol\":\"NEWENTRY\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.045},{\"index\":2,\"geneId\":\"8458\",\"description\":\"transcription termination factor 2\",\"symbol\":\"TTF2\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":10,\"pvalue\":0.0478}],\"hasMoreResults\":true}";
        assertEquals(expectedResponse, response.getBody());

        searchWithSearchQueryJsonObject.put("queryOffset",3);

        request = new HttpEntity<>(searchWithSearchQueryJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Response Body: \"" + response.getBody() + "\"");

        String expectedResponse1 = "{\"searchQuery\":{\"symbol\":[],\"geneId\":[],\"meshTreeId\":[],\"name\":[],\"description\":[],\"meshId\":[\"D003063\"]},\"results\":[{\"index\":3,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.0488},{\"index\":4,\"geneId\":\"8520\",\"description\":\"histone acetyltransferase 1\",\"symbol\":\"HAT1\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":2,\"pvalue\":0.099}],\"hasMoreResults\":false}";
        assertEquals(expectedResponse1, response.getBody());
    }
}
