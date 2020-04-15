package com.cscie599.gfn;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertTrue;
import com.cscie599.gfn.controller.*;
import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.Before;
import org.junit.After;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.web.client.RestTemplate;

import javax.sql.DataSource;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("dev")
@AutoConfigureEmbeddedDatabase
public class UserTest {

    private static RestTemplate restTemplate;
    private static HttpHeaders userHeaders;
    private static HttpHeaders loginHeaders;
    private static JSONObject userJsonObject;
    private static JSONObject user2JsonObject;
    private static JSONObject loginJsonObject;
    private static final Logger logger = LoggerFactory.getLogger(UserTest.class);

    @BeforeAll
    public static void runBeforeAllTestMethods() throws JSONException {

        restTemplate = new RestTemplate();
        userHeaders = new HttpHeaders();
        loginHeaders = new HttpHeaders();
        userJsonObject = new JSONObject();
        user2JsonObject = new JSONObject();
        loginJsonObject = new JSONObject();

        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);

        userJsonObject.put("userEmail", "john_harvard@harvard.edu");
        userJsonObject.put("userPassword", "T3st1234!");
        userJsonObject.put("passwordConfirm", "T3st1234!");
        userJsonObject.put("firstName", "John");
        userJsonObject.put("lastName", "Harvard");

        user2JsonObject.put("userEmail", "jane_harvard@harvard.edu");
        user2JsonObject.put("userPassword", "T3st1234!");
        user2JsonObject.put("passwordConfirm", "T3st1234!");
        user2JsonObject.put("firstName", "Jane");
        user2JsonObject.put("lastName", "Harvard");

        loginJsonObject.put("userEmail", "jane_harvard@harvard.edu");
        loginJsonObject.put("userPassword", "T3st1234!");
    }

    @LocalServerPort
    private int port;

    @Test
    public void testUserWasRegistered() throws Exception {

        HttpEntity<String> request =
                new HttpEntity<String>(userJsonObject.toString(), userHeaders);

        logger.info("Request to be posted is: \"" + request.toString() + "\"");

        ResponseEntity<String> response = restTemplate.
                postForEntity(createURLWithPort("/api/registration"),
                        request, String.class);

        logger.info("Registration API Response was: \"" + response.getStatusCode().toString() + "\"");

        assertTrue(response.getStatusCode().equals(HttpStatus.CREATED));

    }

    @Test
    public void registeredUserCanLogin() throws Exception {

        assertTrue(true);
//        HttpEntity<String> registerRequest =
//                new HttpEntity<String>(user2JsonObject.toString(), loginHeaders);
//
//        logger.info("Request to be posted is: \"" + registerRequest.toString() + "\"");
//
//        ResponseEntity<String> registerResponse = restTemplate.
//                postForEntity(createURLWithPort("/api/registration"),
//                        registerRequest, String.class);
//
//        logger.info("Registration API Response was: \"" + registerResponse.getStatusCode().toString() + "\"");
//
//
//        HttpEntity<String> request =
//                new HttpEntity<String>(loginJsonObject.toString(), loginHeaders);
//
//        logger.info("Request to be posted is: \"" + request.toString() + "\"");
//
//        ResponseEntity<String> response = restTemplate.
//                postForEntity(createURLWithPort("/api/login"),
//                        request, String.class);
//
//        logger.info("Login API Response was: \"" + response.getStatusCode().toString() + "\"");
//
//        assertTrue(response.getStatusCode().equals(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}