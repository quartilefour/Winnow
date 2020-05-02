package com.cscie599.gfn;

import com.cscie599.gfn.controller.JobLauncherController;
import com.cscie599.gfn.views.GeneView;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class GeneControllerTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(GeneControllerTest.class);
    private static TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders httpHeaders;
    private static final JSONObject userJsonObject = new JSONObject();
    private static final JSONObject loginJsonObject = new JSONObject();
    private static final JSONObject expectedResultJsonObject = new JSONObject();

    @Autowired
    private JobLauncherController jobLauncherController;

    @LocalServerPort
    private int port;

    @BeforeClass
    public static void runBeforeAllTestMethods() {

    }

    @Before
    public void setUp() throws Exception {

        httpHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpHeaders registerHttpHeaders = new HttpHeaders();
        registerHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

        jobLauncherController.handle();

        userJsonObject.put("userEmail", "john_harvard@harvard.edu");
        userJsonObject.put("userPassword", "T3st1234!");
        userJsonObject.put("passwordConfirm", "T3st1234!");
        userJsonObject.put("firstName", "John");
        userJsonObject.put("lastName", "Harvard");

        loginJsonObject.put("userEmail", "john_harvard@harvard.edu");
        loginJsonObject.put("userPassword", "T3st1234!");

        expectedResultJsonObject.put("geneId", "20468916");
        expectedResultJsonObject.put("description", "anthranilate synthase large subunit");
        expectedResultJsonObject.put("symbol", "trpE");

        HttpEntity<String> registerRequest =
                new HttpEntity<String>(userJsonObject.toString(), registerHttpHeaders);

        logger.info("Request to be posted is: \"" + registerRequest.toString() + "\"");

        ResponseEntity<String> registerResponse = restTemplate.
                postForEntity(createURLWithPort("/api/registration"),
                        registerRequest, String.class);

        logger.info("Registration API Response was: \"" + registerResponse.getStatusCode().toString() + "\"");

        HttpEntity<String> loginRequest =
                new HttpEntity<String>(loginJsonObject.toString(), httpHeaders);

        logger.info("Request to be posted is: \"" + loginRequest.toString() + "\"");

        ResponseEntity<String> loginResponse = restTemplate.
                postForEntity(createURLWithPort("/api/login"),
                        loginRequest, String.class);

        logger.info("Login API Response was: \"" + loginResponse.getStatusCode().toString() + "\"");

        httpHeaders.set("Authorization", loginResponse.getHeaders().getFirst("Authorization"));

    }

    @Test
    public void canCatchEmptySearch() throws Exception {
        HttpEntity<String> emptySearchRequest =
                new HttpEntity<String>(httpHeaders);

        ArrayList<GeneView> emptyList = new ArrayList<>();

        logger.info("Request to be posted is: \"" + emptySearchRequest.toString() + "\"");

        ResponseEntity<String> emptySearchResponse = restTemplate.exchange(createURLWithPort("/api/genes/search/"),
                HttpMethod.GET, emptySearchRequest, String.class);

        logger.info("[GeneControllerTest - canCatchEmptySearch] API Status Response was: \"" + emptySearchResponse.getStatusCode().toString() + "\"");
        logger.info("[GeneControllerTest - canCatchEmptySearch] API Gene List was: \"" + emptySearchResponse.getBody() + "\"");
        logger.info("[GeneControllerTest - canCatchEmptySearch] Expected String was: \"" + emptyList.toString() + "\"");

        assertThat(emptySearchResponse.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(emptySearchResponse.getBody().equalsIgnoreCase(emptyList.toString()));
    }

    @Test
    public void canCatchPatternSearch() throws Exception {

        final String PATTERN = "tr";

        HttpEntity<String> patternSearchRequest =
                new HttpEntity<String>(httpHeaders);

        logger.info("Request to be posted is: \"" + patternSearchRequest.toString() + "\"");

        ResponseEntity<String> patternSearchResponse = restTemplate.exchange(createURLWithPort("/api/genes/search/" + PATTERN),
                HttpMethod.GET, patternSearchRequest, String.class);

        logger.info("[GeneControllerTest - canCatchPatternSearch] API Status Response was: \"" + patternSearchResponse.getStatusCode().toString() + "\"");
        logger.info("[GeneControllerTest - canCatchPatternSearch] API Gene List was: \"" + patternSearchResponse.getBody() + "\"");
        logger.info("[GeneControllerTest - canCatchPatternSearch] Pattern was: \"" + PATTERN + "\"");

        assertThat(patternSearchResponse.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(patternSearchResponse.getBody().contains(PATTERN));
    }

    @Test
    public void testGetGeneDetailWithNullGeneId() throws JSONException {
        logger.info("testGetGeneDetailWithNullGeneId");
        JSONObject nullGeneIdJsonObject = new JSONObject();
        nullGeneIdJsonObject.put("geneId", JSONObject.NULL);
        HttpEntity<String> request = new HttpEntity<>(nullGeneIdJsonObject.toString(), httpHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/genes"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Gene ID cannot be null.\"}", response.getBody());
    }

    @Test
    public void testGetGeneDetailWithMissingGeneId() throws JSONException {
        logger.info("testGetGeneDetailWithMissingGeneId");
        JSONObject missingGeneIdJsonObject = new JSONObject();
        HttpEntity<String> request = new HttpEntity<>(missingGeneIdJsonObject.toString(), httpHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/genes"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Missing gene ID.\"}", response.getBody());
    }

    @Test
    public void testGetGeneDetailWithBlankGeneId() throws JSONException {
        logger.info("testGetGeneDetailWithBlankGeneId");
        JSONObject blankGeneIdJsonObject = new JSONObject();
        blankGeneIdJsonObject.put("geneId", "");
        HttpEntity<String> request = new HttpEntity<>(blankGeneIdJsonObject.toString(), httpHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/genes"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Gene ID cannot be blank.\"}", response.getBody());
    }

    @Test
    public void testGetGeneDetailWithInvalidGeneId() throws JSONException {
        logger.info("testGetGeneDetailWithInvalidGeneId");
        JSONObject geneIdJsonObject = new JSONObject();
        geneIdJsonObject.put("geneId", "test");
        HttpEntity<String> request = new HttpEntity<>(geneIdJsonObject.toString(), httpHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/genes"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Gene not found.\"}", response.getBody());
    }

    @Test
    public void testGetGeneDetailWithGeneId() throws JSONException {
        logger.info("testGetGeneDetailWithGeneId");
        JSONObject geneIdJsonObject = new JSONObject();
        geneIdJsonObject.put("geneId", "1246505");
        HttpEntity<String> request = new HttpEntity<>(geneIdJsonObject.toString(), httpHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/genes"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        JSONObject responseBody = new JSONObject(response.getBody());
        assertEquals(responseBody.getString("geneId"), "1246505");
        assertEquals(responseBody.getString("symbol"), "leuD");
        assertEquals(responseBody.getString("description"), "isopropylmalate isomerase small subunit");
        logger.info("Response Body Mesh Results: \"" + responseBody.getJSONArray("meshResults").toString() + "\"");
        assertThat(responseBody.getJSONArray("meshResults") instanceof List);
        assertEquals(responseBody.getJSONArray("meshResults").length(), 3);
        logger.info("Response Body First Mesh Result: \"" + responseBody.getJSONArray("meshResults").get(0) + "\"");
        JSONObject firstMeshResult = new JSONObject(responseBody.getJSONArray("meshResults").get(0).toString());
        assertEquals(firstMeshResult.getString("publicationCount"), "13");
        assertEquals(firstMeshResult.getString("name"), "Animals");
        assertEquals(firstMeshResult.getString("pvalue"), "0.001");
        assertEquals(firstMeshResult.getString("meshId"), "D000818");
        logger.info("Response Body Co-occurring Gene Results: \"" + responseBody.getJSONArray("geneResults").toString() + "\"");
        assertThat(responseBody.getJSONArray("geneResults") instanceof List);
        assertEquals(responseBody.getJSONArray("geneResults").length(), 17);
    }

    private String createURLWithPort(String uri) {
        return ("http://localhost:" + port + uri);
    }

}