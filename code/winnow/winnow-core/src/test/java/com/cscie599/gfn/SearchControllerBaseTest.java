package com.cscie599.gfn;

import com.cscie599.gfn.controller.JobLauncherController;
import com.cscie599.gfn.controller.SearchController;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

/**
 * @author PulkitBhanot
 */
public abstract class SearchControllerBaseTest extends BaseTest {

    @Autowired
    JobLauncherController jobLauncherController;

    @Autowired
    SearchController searchController;

    protected static TestRestTemplate testRestTemplate;
    protected static HttpHeaders userHeaders;
    protected static HttpHeaders loginHeaders;
    protected static HttpHeaders searchHeaders;
    protected static HttpHeaders fileHeaders;
    protected static JSONObject userJsonObject;
    protected static JSONObject loginJsonObject;
    protected static JSONObject emptySearchQueryJsonObject;
    protected static JSONObject geneIdsSearchQueryJsonObject;
    protected static JSONObject meshIdsSearchQueryJsonObject;
    protected static JSONObject genesSearchQueryJsonObject;
    protected static JSONObject meshtermsSearchQueryJsonObject;
    protected static JSONObject genesAndMeshtermsSearchQueryJsonObject;
    protected static final Logger logger = LoggerFactory.getLogger(SearchControllerBaseTest.class);

    @LocalServerPort
    protected int port;

    public String createURLWithPort(String uri) {
        return "http://localhost:" + port + uri;
    }

    protected static void initialize() throws JSONException {
        testRestTemplate = new TestRestTemplate();
        userHeaders = new HttpHeaders();
        loginHeaders = new HttpHeaders();
        searchHeaders = new HttpHeaders();
        fileHeaders = new HttpHeaders();
        userJsonObject = new JSONObject();
        loginJsonObject = new JSONObject();
        userHeaders.setContentType(MediaType.APPLICATION_JSON);
        loginHeaders.setContentType(MediaType.APPLICATION_JSON);
        searchHeaders.setContentType(MediaType.APPLICATION_JSON);
        fileHeaders.setContentType(MediaType.MULTIPART_FORM_DATA);
        userJsonObject.put("userEmail", "bob_harvard@harvard.edu");
        userJsonObject.put("userPassword", "T3st1234!");
        userJsonObject.put("passwordConfirm", "T3st1234!");
        userJsonObject.put("firstName", "Bob");
        userJsonObject.put("lastName", "Harvard");
        loginJsonObject.put("userEmail", "bob_harvard@harvard.edu");
        loginJsonObject.put("userPassword", "T3st1234!");
        JSONArray geneIds = new JSONArray();
        JSONArray descriptions = new JSONArray();
        JSONArray symbols = new JSONArray();
        JSONArray meshIds = new JSONArray();
        JSONArray meshTreeIds = new JSONArray();
        JSONArray names = new JSONArray();
        JSONArray emptyArray = new JSONArray();
        /*
        geneIds.put("5692769");
        descriptions.put("Record to support submission of GeneRIFs for a gene not in Gene (Azotirhizobium caulinodans.  Use when strain, subtype, isolate, etc. is unspecified, or when different from all specified ones in Gene.).");
        symbols.put("NEWENTRY");
        */
        geneIds.put("1246505");
        descriptions.put("isopropylmalate isomerase small subunit");
        symbols.put("leuD");
        /*
        geneIds.put("8655732");
        descriptions.put("3-isopropylmalate dehydratase large subunit; leucine biosinthesis");
        symbols.put("leuC");
        */
        /*
        meshIds.put("D000818");
        names.put("Animals");
        meshTreeIds.put("B01.050");
        */
        /*
        meshIds.put("D003201");
        names.put("Computers");
        meshTreeIds.put("L01.224.230.260");
        */
        meshIds.put("D003063");
        names.put("Cnidaria");
        meshTreeIds.put("B01.050.500.308");
        /*
        meshIds.put("D056890");
        names.put("Eukaryota");
        meshTreeIds.put("B01");
        */
        emptySearchQueryJsonObject = new JSONObject();
        emptySearchQueryJsonObject.put("geneId", emptyArray);
        emptySearchQueryJsonObject.put("description", emptyArray);
        emptySearchQueryJsonObject.put("symbol", emptyArray);
        emptySearchQueryJsonObject.put("meshId", emptyArray);
        emptySearchQueryJsonObject.put("meshTreeId", emptyArray);
        emptySearchQueryJsonObject.put("name", emptyArray);
        geneIdsSearchQueryJsonObject = new JSONObject();
        geneIdsSearchQueryJsonObject.put("geneId", geneIds);
        geneIdsSearchQueryJsonObject.put("description", emptyArray);
        geneIdsSearchQueryJsonObject.put("symbol", emptyArray);
        geneIdsSearchQueryJsonObject.put("meshId", emptyArray);
        geneIdsSearchQueryJsonObject.put("meshTreeId", emptyArray);
        geneIdsSearchQueryJsonObject.put("name", emptyArray);
        meshIdsSearchQueryJsonObject = new JSONObject();
        meshIdsSearchQueryJsonObject.put("geneId", emptyArray);
        meshIdsSearchQueryJsonObject.put("description", emptyArray);
        meshIdsSearchQueryJsonObject.put("symbol", emptyArray);
        meshIdsSearchQueryJsonObject.put("meshId", meshIds);
        meshIdsSearchQueryJsonObject.put("meshTreeId", emptyArray);
        meshIdsSearchQueryJsonObject.put("name", emptyArray);
        genesSearchQueryJsonObject = new JSONObject();
        genesSearchQueryJsonObject.put("geneId", geneIds);
        genesSearchQueryJsonObject.put("description", descriptions);
        genesSearchQueryJsonObject.put("symbol", symbols);
        genesSearchQueryJsonObject.put("meshId", emptyArray);
        genesSearchQueryJsonObject.put("meshTreeId", emptyArray);
        genesSearchQueryJsonObject.put("name", emptyArray);
        meshtermsSearchQueryJsonObject = new JSONObject();
        meshtermsSearchQueryJsonObject.put("geneId", emptyArray);
        meshtermsSearchQueryJsonObject.put("description", emptyArray);
        meshtermsSearchQueryJsonObject.put("symbol", emptyArray);
        meshtermsSearchQueryJsonObject.put("meshId", meshIds);
        meshtermsSearchQueryJsonObject.put("meshTreeId", meshTreeIds);
        meshtermsSearchQueryJsonObject.put("name", names);
        genesAndMeshtermsSearchQueryJsonObject = new JSONObject();
        genesAndMeshtermsSearchQueryJsonObject.put("geneId", geneIds);
        genesAndMeshtermsSearchQueryJsonObject.put("description", descriptions);
        genesAndMeshtermsSearchQueryJsonObject.put("symbol", symbols);
        genesAndMeshtermsSearchQueryJsonObject.put("meshId", meshIds);
        genesAndMeshtermsSearchQueryJsonObject.put("meshTreeId", meshTreeIds);
        genesAndMeshtermsSearchQueryJsonObject.put("name", names);
    }
}
