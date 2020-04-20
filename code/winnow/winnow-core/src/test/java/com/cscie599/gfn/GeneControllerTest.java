package com.cscie599.gfn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import com.cscie599.gfn.controller.JobLauncherController;
import com.cscie599.gfn.entities.Gene;
import com.cscie599.gfn.views.GeneView;
import io.swagger.annotations.ApiOperation;
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
import org.springframework.core.env.Environment;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.ArrayList;
import java.util.List;

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

    @Autowired
    private Environment env;

    @LocalServerPort
    private int port;

    @BeforeClass
    public static void runBeforeAllTestMethods() throws JSONException {

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
    public void canFindAllGenes() throws Exception {

        HttpEntity<String> allGeneRequest =
                new HttpEntity<String>(httpHeaders);

        logger.info("Request to be posted is: \"" + allGeneRequest.toString() + "\"");

        ResponseEntity<String> allGeneResponse = restTemplate.exchange(createURLWithPort("/api/genes"),
                HttpMethod.GET, allGeneRequest, String.class);

        logger.info("[GeneControllerTest - canFindAllGenes] API Status Response was: \"" + allGeneResponse.getStatusCode().toString() + "\"");
        logger.info("[GeneControllerTest - canFindAllGenes] API Gene List was: \"" + allGeneResponse.getBody() + "\"");

        assertThat(allGeneResponse.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }


    @Test
    public void canFindSpecificGene() throws Exception {

        HttpEntity<String> oneGeneRequest =
                new HttpEntity<String>(httpHeaders);

        logger.info("Request to be posted is: \"" + oneGeneRequest.toString() + "\"");


        ResponseEntity<String> oneGeneResponse = restTemplate.exchange(createURLWithPort("/api/genes/" + expectedResultJsonObject.get("geneId")),
                HttpMethod.GET, oneGeneRequest, String.class);

        logger.info("[GeneControllerTest - canFindSpecificGene] API Status Response was: \"" + oneGeneResponse.getStatusCode().toString() + "\"");
        logger.info("[GeneControllerTest - canFindSpecificGene] API Gene List was: \"" + oneGeneResponse.getBody() + "\"");
        logger.info("[GeneControllerTest - canFindSpecificGene] Expected JSON String was: \"" + expectedResultJsonObject.toString() + "\"");

        assertThat(oneGeneResponse.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
        assertThat(oneGeneResponse.getBody().contains(expectedResultJsonObject.get("symbol").toString()));
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

    private String createURLWithPort(String uri) {
        return ("http://localhost:" + port + uri);
    }

}