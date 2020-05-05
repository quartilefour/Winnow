package com.cscie599.gfn;

import com.cscie599.gfn.controller.JobLauncherController;
import com.cscie599.gfn.controller.MeshtermController;
import com.cscie599.gfn.views.MeshtermCategoryView;
import com.cscie599.gfn.views.MeshtermTreeView;
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
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MeshtermControllerTest extends BaseTest {

    private static final Logger logger = LoggerFactory.getLogger(MeshtermControllerTest.class);
    private static TestRestTemplate testRestTemplate;
    private static HttpHeaders userHeaders;
    private static HttpHeaders loginHeaders;
    private static JSONObject userJsonObject;
    private static JSONObject loginJsonObject;
    private static boolean isSetUp = false;
    @Autowired
    JobLauncherController jobLauncherController;
    @Autowired
    MeshtermController meshtermController;
    @LocalServerPort
    private int port;

    @BeforeClass
    public static void runBeforeAllTestMethods() throws JSONException {
        testRestTemplate = new TestRestTemplate();
        userHeaders = new HttpHeaders();
        loginHeaders = new HttpHeaders();
        userJsonObject = new JSONObject();
        loginJsonObject = new JSONObject();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        userJsonObject.put("userEmail", "joe_harvard@harvard.edu");
        userJsonObject.put("userPassword", "T3st1234!");
        userJsonObject.put("passwordConfirm", "T3st1234!");
        userJsonObject.put("firstName", "Joe");
        userJsonObject.put("lastName", "Harvard");
        loginJsonObject.put("userEmail", "joe_harvard@harvard.edu");
        loginJsonObject.put("userPassword", "T3st1234!");
    }

    @Before
    public void setUp() throws Exception {
        if (!(isSetUp)) {
            HttpEntity<String> registerRequest = new HttpEntity<>(userJsonObject.toString(), userHeaders);
            testRestTemplate.postForEntity(createURLWithPort("/api/registration"), registerRequest, String.class);
            HttpEntity<String> loginRequest = new HttpEntity<>(loginJsonObject.toString(), loginHeaders);
            testRestTemplate.postForEntity(createURLWithPort("/api/login"), loginRequest, String.class);
            jobLauncherController.handle();
        }
        isSetUp = true;
    }

    @Test
    public void testGetAllCategories() {
        logger.info("testGetAllCategories");
        List<MeshtermCategoryView> meshtermCategoryViews = meshtermController.findAllCategories();
        assertEquals("Meshterm category count", meshtermCategoryViews.size(), 16);
        assertEquals("A", meshtermCategoryViews.get(0).getCategoryId());
        assertEquals("Anatomy", meshtermCategoryViews.get(0).getName());
    }

    @Test
    public void testFindAllTreesByTreeNodeIdStartingWith() {
        logger.info("testFindAllTreesByTreeNodeIdStartingWith");
        List<MeshtermTreeView> meshtermTreeViews = meshtermController.findAllTreesByTreeNodeIdStartingWith("260");
        assertEquals("Meshterm tree count", meshtermTreeViews.size(), 1);
        assertEquals("D003201", meshtermTreeViews.get(0).getMeshId());
        assertEquals("L01.224.230", meshtermTreeViews.get(0).getTreeParentId());
        assertEquals("260", meshtermTreeViews.get(0).getTreeNodeId());
        assertEquals("Computers [L01.224.230.260]", meshtermTreeViews.get(0).getMeshName());
    }

    @Test
    public void testFindAllTreesByParentNodeId() {
        logger.info("testFindAllTreesByParentNodeId");
        List<MeshtermTreeView> meshtermTreeViews = meshtermController.findAllTreesByParentNodeId("L01.224.230");
        assertEquals("Meshterm tree count", meshtermTreeViews.size(), 1);
        assertEquals("D003201", meshtermTreeViews.get(0).getMeshId());
        assertEquals("L01.224.230", meshtermTreeViews.get(0).getTreeParentId());
        assertEquals("260", meshtermTreeViews.get(0).getTreeNodeId());
        assertEquals("Computers [L01.224.230.260]", meshtermTreeViews.get(0).getMeshName());
    }

    @Test
    public void testFindAllTrees() {
        logger.info("testFindAllTrees");
        List<MeshtermTreeView> meshtermTreeViews = new ArrayList(meshtermController.findEntireTree());
        assertEquals("Meshterm tree count", meshtermTreeViews.size(), 16);
        assertEquals("A", meshtermTreeViews.get(0).getMeshId());
        assertEquals("Anatomy [A]", meshtermTreeViews.get(0).getMeshName());
        assertEquals("B", meshtermTreeViews.get(1).getMeshId());
        assertEquals("Organisms [B]", meshtermTreeViews.get(1).getMeshName());
    }

    public String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

}
