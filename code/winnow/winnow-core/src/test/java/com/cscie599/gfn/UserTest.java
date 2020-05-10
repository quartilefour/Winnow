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
        userJsonObject = new JSONObject();
        user2JsonObject = new JSONObject();
        user3JsonObject = new JSONObject();
        loginJsonObject = new JSONObject();
        login2JsonObject = new JSONObject();

        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        profileHeaders.setContentType(MediaType.APPLICATION_JSON);

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
        JSONObject updateProfileJsonObject1 = new JSONObject();
        updateProfileJsonObject1.put("userPassword", "T3st1234!");
        updateProfileJsonObject1.put("userPasswordNew", "Test1234!");
        updateProfileJsonObject1.put("passwordConfirm", "Test1234!");
        HttpEntity<String> request1 = new HttpEntity<>(updateProfileJsonObject1.toString(), profileHeaders);
        ResponseEntity<String> response1 = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request1, String.class);
        logger.info("Response Body: \"" + response1.getBody() + "\"");
        assertEquals("{\"success\":\"Password changed.\"}", response1.getBody());
        JSONObject updateProfileJsonObject2 = new JSONObject();
        updateProfileJsonObject2.put("userPassword", "Test1234!");
        updateProfileJsonObject2.put("userPasswordNew", "T3st1234!");
        updateProfileJsonObject2.put("passwordConfirm", "T3st1234!");
        HttpEntity<String> request2 = new HttpEntity<>(updateProfileJsonObject2.toString(), profileHeaders);
        ResponseEntity<String> response2 = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request2, String.class);
        logger.info("Response Body: \"" + response2.getBody() + "\"");
        assertEquals("{\"success\":\"Password changed.\"}", response2.getBody());
    }

    @Test
    public void testChangePasswordWithWrongCurrentPassword() throws Exception {
        logger.info("testChangePasswordWithWrongCurrentPassword");
        JSONObject updateProfileJsonObject = new JSONObject();
        updateProfileJsonObject.put("userPassword", "Test1234!");
        updateProfileJsonObject.put("userPasswordNew", "T3st1234!");
        updateProfileJsonObject.put("passwordConfirm", "T3st1234!");
        HttpEntity<String> request = new HttpEntity<>(updateProfileJsonObject.toString(), profileHeaders);
        ResponseEntity<String> response = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Wrong current password.\"}", response.getBody());
    }

    @Test
    public void testChangePasswordWithInvalidNewPassword() throws Exception {
        logger.info("testChangePasswordWithInvalidNewPassword");
        JSONObject updateProfileJsonObject = new JSONObject();
        updateProfileJsonObject.put("userPassword", "T3st1234!");
        updateProfileJsonObject.put("userPasswordNew", "test");
        updateProfileJsonObject.put("passwordConfirm", "test");
        HttpEntity<String> request = new HttpEntity<>(updateProfileJsonObject.toString(), profileHeaders);
        ResponseEntity<String> response = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Password must be between 8 and 60 characters.\"}", response.getBody());
    }

    @Test
    public void testChangePasswordWithInvalidConfirmPassword() throws Exception {
        logger.info("testChangePasswordWithInvalidConfirmPassword");
        JSONObject updateProfileJsonObject = new JSONObject();
        updateProfileJsonObject.put("userPassword", "T3st1234!");
        updateProfileJsonObject.put("userPasswordNew", "Test1234!");
        updateProfileJsonObject.put("passwordConfirm", "Test12345!");
        HttpEntity<String> request = new HttpEntity<>(updateProfileJsonObject.toString(), profileHeaders);
        ResponseEntity<String> response = patchRestTemplate.exchange(createURLWithPort("/api/profile"), HttpMethod.PATCH, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Password does not match.\"}", response.getBody());
    }

    @Test
    public void testForgotPasswordWithMissingUserEmail() throws Exception {
        logger.info("testForgotPasswordWithMissingUserEmail");
        JSONObject forgotPasswordJsonObject = new JSONObject();
        HttpEntity<String> request = new HttpEntity<>(forgotPasswordJsonObject.toString(), profileHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/forgot"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Missing userEmail.\"}", response.getBody());
    }

    @Test
    public void testForgotPasswordWithUserEmail() throws Exception {
        logger.info("testForgotPasswordWithUserEmail");
        JSONObject forgotPasswordJsonObject = new JSONObject();
        forgotPasswordJsonObject.put("userEmail", "test1234@harvard.edu");
        HttpEntity<String> request = new HttpEntity<>(forgotPasswordJsonObject.toString(), profileHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/forgot"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"success\":\"Please check your email for password reset link.\"}", response.getBody());
    }

    @Test
    public void testCheckPasswordResetLink() throws Exception {
        logger.info("testCheckPasswordResetLink");
        HttpEntity<String> request = new HttpEntity<>(profileHeaders);
        ResponseEntity<String> response = restTemplate.exchange(createURLWithPort("/api/reset?token=test"),
                HttpMethod.GET, request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
    }

    @Test
    public void testResetPasswordWithMissingToken() throws Exception {
        logger.info("testResetPasswordWithMissingToken");
        JSONObject resetPasswordJsonObject = new JSONObject();
        resetPasswordJsonObject.put("userPasswordNew", "Test1234!");
        resetPasswordJsonObject.put("passwordConfirm", "Test1234!");
        HttpEntity<String> request = new HttpEntity<>(resetPasswordJsonObject.toString(), profileHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/reset"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Missing token.\"}", response.getBody());
    }

    @Test
    public void testResetPasswordWithInvalidToken() throws Exception {
        logger.info("testResetPasswordWithInvalidToken");
        JSONObject resetPasswordJsonObject = new JSONObject();
        resetPasswordJsonObject.put("token", "test");
        resetPasswordJsonObject.put("userPasswordNew", "Test1234!");
        resetPasswordJsonObject.put("passwordConfirm", "Test1234!");
        HttpEntity<String> request = new HttpEntity<>(resetPasswordJsonObject.toString(), profileHeaders);
        ResponseEntity<String> response = restTemplate.postForEntity(createURLWithPort("/api/reset"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Invalid password reset link.\"}", response.getBody());
    }

    private String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}