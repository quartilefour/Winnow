package com.cscie599.gfn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;

import com.cscie599.gfn.controller.JobLauncherController;
import com.cscie599.gfn.controller.PublicationController;
import com.cscie599.gfn.entities.Gene;
import com.cscie599.gfn.views.GeneView;
import io.swagger.annotations.ApiOperation;
import org.hibernate.Hibernate;
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

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class PublicationControllerTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(PublicationControllerTest.class);
    private static TestRestTemplate restTemplate = new TestRestTemplate();
    private HttpHeaders httpHeaders;
    private HttpHeaders searchHeaders;
    private static final JSONObject userJsonObject = new JSONObject();
    private static final JSONObject loginJsonObject = new JSONObject();
    private static final JSONObject expectedResultJsonObject = new JSONObject();

    @Autowired
    private JobLauncherController jobLauncherController;

    @Autowired
    private PublicationController publicationController;

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
        searchHeaders = new HttpHeaders();
        httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        searchHeaders.setContentType(MediaType.APPLICATION_JSON);

        HttpHeaders registerHttpHeaders = new HttpHeaders();
        registerHttpHeaders.setContentType(MediaType.APPLICATION_JSON);

        jobLauncherController.handle();

        userJsonObject.put("userEmail", "kojo_harvard@harvard.edu");
        userJsonObject.put("userPassword", "T3st1234!");
        userJsonObject.put("passwordConfirm", "T3st1234!");
        userJsonObject.put("firstName", "Kojo");
        userJsonObject.put("lastName", "Harvard");

        loginJsonObject.put("userEmail", "kojo_harvard@harvard.edu");
        loginJsonObject.put("userPassword", "T3st1234!");

        expectedResultJsonObject.put("geneId", "20468916");
        expectedResultJsonObject.put("meshId", "D000818");

        if(httpHeaders.get("Authorization") == null) {

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

    }

    @Test
    public void canFindExpectedPublications() throws Exception {

        JSONObject jsonObject;

        HttpEntity<String> expectedPublicationRequest =
                new HttpEntity<String>(expectedResultJsonObject.toString(), httpHeaders);

        logger.info("Request to be posted is: \"" + expectedPublicationRequest.toString() + "\"");

        ResponseEntity<String> expectedPublicationResponse = restTemplate.postForEntity(createURLWithPort("/api/publications"),
                expectedPublicationRequest, String.class);

        logger.info("[PublicationControllerTest - canFindExpectedPublications] API Status Response was: \"" + expectedPublicationResponse.getStatusCode().toString() + "\"");
        logger.info("[PublicationControllerTest - canFindExpectedPublications] API Publication List was: \"" + expectedPublicationResponse.getBody() + "\"");

        jsonObject = new JSONObject(expectedPublicationResponse.getBody());
        assertThat(jsonObject.has("results"));
        assertThat(jsonObject.getJSONArray("results").length() == 2);
        assertThat(expectedPublicationResponse.getStatusCode()).isEqualByComparingTo(HttpStatus.OK);
    }

    private String createURLWithPort(String uri) {
        return ("http://localhost:" + port + uri);
    }

}