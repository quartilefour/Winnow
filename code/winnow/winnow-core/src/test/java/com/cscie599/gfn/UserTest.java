package com.cscie599.gfn;

import org.apache.http.client.HttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.*;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class UserTest extends BaseTest {

    private static TestRestTemplate restTemplate;
    private static RestTemplate patchRestTemplate;
    private static HttpHeaders userHeaders;
    private static HttpHeaders loginHeaders;
    private static HttpHeaders profileHeaders;
    private static HttpHeaders patchHeaders;
    private static JSONObject userJsonObject;
    private static JSONObject user2JsonObject;
    private static JSONObject user3JsonObject;
    private static JSONObject loginJsonObject;
    private static JSONObject login2JsonObject;
    private static boolean isSetUp = false;
    private static final Logger logger = LoggerFactory.getLogger(UserTest.class);

    @BeforeClass
    public static void runBeforeAllTestMethods() throws JSONException {

        restTemplate = new TestRestTemplate();
        userHeaders = new HttpHeaders();
        loginHeaders = new HttpHeaders();
        profileHeaders = new HttpHeaders();
        patchHeaders = new HttpHeaders();
        userJsonObject = new JSONObject();
        user2JsonObject = new JSONObject();
        user3JsonObject = new JSONObject();
        loginJsonObject = new JSONObject();
        login2JsonObject = new JSONObject();

        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        profileHeaders.setContentType(MediaType.APPLICATION_JSON);
        patchHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        userJsonObject.put("userEmail", "frank_harvard@harvard.edu");
        userJsonObject.put("userPassword", "T3st1234!");
        userJsonObject.put("passwordConfirm", "T3st1234!");
        userJsonObject.put("firstName", "Frank");
        userJsonObject.put("lastName", "Harvard");

        user2JsonObject.put("userEmail", "jane_harvard@harvard.edu");
        user2JsonObject.put("userPassword", "T3st1234!");
        user2JsonObject.put("passwordConfirm", "T3st1234!");
        user2JsonObject.put("firstName", "Jane");
        user2JsonObject.put("lastName", "Harvard");

        user3JsonObject.put("userEmail", "joe_harvard@harvard.edu");
        user3JsonObject.put("userPassword", "T3st1234!");
        user3JsonObject.put("passwordConfirm", "T3st1234!");
        user3JsonObject.put("firstName", "Joe");
        user3JsonObject.put("lastName", "Harvard");

        loginJsonObject.put("userEmail", "jane_harvard@harvard.edu");
        loginJsonObject.put("userPassword", "T3st1234!");

        login2JsonObject.put("userEmail", "joe_harvard@harvard.edu");
        login2JsonObject.put("userPassword", "T3st1234!");
    }

    @Before
    public void setUp() throws Exception {
        if (!(isSetUp)) {
            HttpEntity<String> registerRequest = new HttpEntity<>(user3JsonObject.toString(), userHeaders);
            restTemplate.postForEntity(createURLWithPort("/api/registration"), registerRequest, String.class);
            HttpEntity<String> loginRequest = new HttpEntity<>(login2JsonObject.toString(), loginHeaders);
            ResponseEntity<String> loginResponse = restTemplate.postForEntity(createURLWithPort("/api/login"), loginRequest, String.class);
            profileHeaders.set("Authorization", loginResponse.getHeaders().getFirst("Authorization"));
            patchRestTemplate = restTemplate.getRestTemplate();
            HttpClient httpClient = HttpClientBuilder.create().build();
            patchRestTemplate.setRequestFactory(new HttpComponentsClientHttpRequestFactory(httpClient));
            patchHeaders.set("Authorization", loginResponse.getHeaders().getFirst("Authorization"));
        }
        isSetUp = true;
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

        HttpEntity<String> registerRequest =
                new HttpEntity<String>(user2JsonObject.toString(), loginHeaders);

        logger.info("Request to be posted is: \"" + registerRequest.toString() + "\"");

        ResponseEntity<String> registerResponse = restTemplate.
                postForEntity(createURLWithPort("/api/registration"),
                        registerRequest, String.class);

        logger.info("Registration API Response was: \"" + registerResponse.getStatusCode().toString() + "\"");

        assertTrue(registerResponse.getStatusCode().equals(HttpStatus.CREATED));

        HttpEntity<String> request =
                new HttpEntity<String>(loginJsonObject.toString(), loginHeaders);

        logger.info("Request to be posted is: \"" + request.toString() + "\"");

        ResponseEntity<String> response = restTemplate.
                postForEntity(createURLWithPort("/api/login"),
                        request, String.class);

        logger.info("Login API Response was: \"" + response.getStatusCode().toString() + "\"");

        assertTrue(response.getStatusCode().equals(HttpStatus.OK));
    }

    @Test
    public void testRegistrationWithExistingUserEmail() throws Exception {
        HttpEntity<String> registerRequest = new HttpEntity<>(user3JsonObject.toString(), userHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/registration"), registerRequest, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Email already exists.\"}", response.getBody());
    }

    @Test
    public void testGetProfile() throws Exception {
        logger.info("testGetProfile");
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.GET, new HttpEntity<>(profileHeaders), String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        JSONObject responseBody = new JSONObject(response.getBody());
        assertEquals(responseBody.getString("userEmail"), "joe_harvard@harvard.edu");
        assertEquals(responseBody.getString("firstName"), "Joe");
        assertEquals(responseBody.getString("lastName"), "Harvard");
    }

    @Test
    public void testUpdateProfile() throws Exception {
        logger.info("testUpdateProfile");
        JSONObject updateProfileJsonObject = new JSONObject();
        updateProfileJsonObject.put("userEmail", "joe_harvard@harvard.edu");
        updateProfileJsonObject.put("firstName", "Jon");
        updateProfileJsonObject.put("lastName", "Smith");
        HttpEntity<String> request = new HttpEntity<>(updateProfileJsonObject.toString(), profileHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PUT, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        JSONObject responseBody = new JSONObject(response.getBody());
        assertEquals(responseBody.getString("userEmail"), "joe_harvard@harvard.edu");
        assertEquals(responseBody.getString("firstName"), "Jon");
        assertEquals(responseBody.getString("lastName"), "Smith");
    }

    @Test
    public void testUpdateProfileWithInvalidEmail() throws Exception {
        logger.info("testUpdateProfileWithInvalidEmail");
        JSONObject updateProfileJsonObject = new JSONObject();
        updateProfileJsonObject.put("userEmail", "test");
        updateProfileJsonObject.put("firstName", "Jon");
        updateProfileJsonObject.put("lastName", "Smith");
        HttpEntity<String> request = new HttpEntity<>(updateProfileJsonObject.toString(), profileHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PUT, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Invalid email, please try again.\"}", response.getBody());
    }

    @Test
    public void testChangePassword() throws Exception {
        logger.info("testChangePassword");
        MultiValueMap<String, String> map1 = new LinkedMultiValueMap<>();
        map1.add("userPassword", "T3st1234!");
        map1.add("userPasswordNew", "Test1234!");
        map1.add("passwordConfirm", "Test1234!");
        HttpEntity<MultiValueMap<String, String>> request1 = new HttpEntity<>(map1, patchHeaders);
        ResponseEntity<String> response1 = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request1, String.class);
        logger.info("Response Body: \"" + response1.getBody() + "\"");
        assertEquals("{\"success\":\"Password changed.\"}", response1.getBody());
        MultiValueMap<String, String> map2 = new LinkedMultiValueMap<>();
        map2.add("userPassword", "Test1234!");
        map2.add("userPasswordNew", "T3st1234!");
        map2.add("passwordConfirm", "T3st1234!");
        HttpEntity<MultiValueMap<String, String>> request2 = new HttpEntity<>(map2, patchHeaders);
        ResponseEntity<String> response2 = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request2, String.class);
        logger.info("Response Body: \"" + response2.getBody() + "\"");
        assertEquals("{\"success\":\"Password changed.\"}", response2.getBody());
    }

    @Test
    public void testChangePasswordWithWrongCurrentPassword() throws Exception {
        logger.info("testChangePasswordWithWrongCurrentPassword");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("userPassword", "Test1234!");
        map.add("userPasswordNew", "T3st1234!");
        map.add("passwordConfirm", "T3st1234!");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, patchHeaders);
        ResponseEntity<String> response = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Wrong current password.\"}", response.getBody());
    }

    @Test
    public void testChangePasswordWithInvalidNewPassword() throws Exception {
        logger.info("testChangePasswordWithInvalidNewPassword");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("userPassword", "T3st1234!");
        map.add("userPasswordNew", "test");
        map.add("passwordConfirm", "test");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, patchHeaders);
        ResponseEntity<String> response = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Password must be between 8 and 60 characters.\"}", response.getBody());
    }

    @Test
    public void testChangePasswordWithInvalidConfirmPassword() throws Exception {
        logger.info("testChangePasswordWithInvalidConfirmPassword");
        MultiValueMap<String, String> map = new LinkedMultiValueMap<>();
        map.add("userPassword", "T3st1234!");
        map.add("userPasswordNew", "Test1234!");
        map.add("passwordConfirm", "Test12345!");
        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(map, patchHeaders);
        ResponseEntity<String> response = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Password does not match.\"}", response.getBody());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}