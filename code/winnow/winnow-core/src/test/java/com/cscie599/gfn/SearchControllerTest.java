package com.cscie599.gfn;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SearchControllerTest extends SearchControllerBaseTest {

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
    public void testExecuteSearchWithGeneIds() throws JSONException {
        logger.info("testExecuteSearchWithGeneIds");
        JSONObject searchWithSearchQueryJsonObject = new JSONObject();
        searchWithSearchQueryJsonObject.put("searchQuery", geneIdsSearchQueryJsonObject);
        HttpEntity<String> request = new HttpEntity<>(searchWithSearchQueryJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        String expectedResponse = "{\"searchQuery\":{\"symbol\":[],\"geneId\":[\"1246505\"],\"meshTreeId\":[],\"name\":[],\"description\":[],\"meshId\":[]},\"results\":[{\"index\":0,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D000818\",\"name\":\"Animals\",\"publicationCount\":13,\"pvalue\":0.001},{\"index\":1,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.0488},{\"index\":2,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003201\",\"name\":\"Computers\",\"publicationCount\":1,\"pvalue\":0.5433}],\"hasMoreResults\":false}";
        assertEquals(expectedResponse, response.getBody());
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
        String expectedResponse = "{\"searchQuery\":{\"symbol\":[],\"geneId\":[],\"meshTreeId\":[],\"name\":[],\"description\":[],\"meshId\":[\"D003063\"]},\"results\":[{\"index\":0,\"geneId\":\"8655732\",\"description\":\"3-isopropylmalate dehydratase large subunit; leucine biosinthesis\",\"symbol\":\"leuC\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":0,\"pvalue\":0.0095},{\"index\":1,\"geneId\":\"5692769\",\"description\":\"Record to support submission of GeneRIFs for a gene not in Gene (Azotirhizobium caulinodans.  Use when strain, subtype, isolate, etc. is unspecified, or when different from all specified ones in Gene.).\",\"symbol\":\"NEWENTRY\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.045},{\"index\":2,\"geneId\":\"8458\",\"description\":\"transcription termination factor 2\",\"symbol\":\"TTF2\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":10,\"pvalue\":0.0478},{\"index\":3,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.0488},{\"index\":4,\"geneId\":\"8520\",\"description\":\"histone acetyltransferase 1\",\"symbol\":\"HAT1\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":2,\"pvalue\":0.099}],\"hasMoreResults\":false}";
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testExecuteSearchWithGenes() throws JSONException {
        logger.info("testExecuteSearchWithGenes");
        JSONObject searchWithSearchQueryJsonObject = new JSONObject();
        searchWithSearchQueryJsonObject.put("searchQuery", genesSearchQueryJsonObject);
        HttpEntity<String> request = new HttpEntity<>(searchWithSearchQueryJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        String expectedResponse = "{\"searchQuery\":{\"symbol\":[\"leuD\"],\"geneId\":[\"1246505\"],\"meshTreeId\":[],\"name\":[],\"description\":[\"isopropylmalate isomerase small subunit\"],\"meshId\":[]},\"results\":[{\"index\":0,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D000818\",\"name\":\"Animals\",\"publicationCount\":13,\"pvalue\":0.001},{\"index\":1,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.0488},{\"index\":2,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003201\",\"name\":\"Computers\",\"publicationCount\":1,\"pvalue\":0.5433}],\"hasMoreResults\":false}";
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testExecuteSearchWithMeshterms() throws JSONException {
        logger.info("testExecuteSearchWithMeshterms");
        JSONObject searchWithSearchQueryJsonObject = new JSONObject();
        searchWithSearchQueryJsonObject.put("searchQuery", meshtermsSearchQueryJsonObject);
        HttpEntity<String> request = new HttpEntity<>(searchWithSearchQueryJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        String expectedResponse = "{\"searchQuery\":{\"symbol\":[],\"geneId\":[],\"meshTreeId\":[\"B01.050.500.308\"],\"name\":[\"Cnidaria\"],\"description\":[],\"meshId\":[\"D003063\"]},\"results\":[{\"index\":0,\"geneId\":\"8655732\",\"description\":\"3-isopropylmalate dehydratase large subunit; leucine biosinthesis\",\"symbol\":\"leuC\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":0,\"pvalue\":0.0095},{\"index\":1,\"geneId\":\"5692769\",\"description\":\"Record to support submission of GeneRIFs for a gene not in Gene (Azotirhizobium caulinodans.  Use when strain, subtype, isolate, etc. is unspecified, or when different from all specified ones in Gene.).\",\"symbol\":\"NEWENTRY\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.045},{\"index\":2,\"geneId\":\"8458\",\"description\":\"transcription termination factor 2\",\"symbol\":\"TTF2\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":10,\"pvalue\":0.0478},{\"index\":3,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.0488},{\"index\":4,\"geneId\":\"8520\",\"description\":\"histone acetyltransferase 1\",\"symbol\":\"HAT1\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":2,\"pvalue\":0.099}],\"hasMoreResults\":false}";
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testExecuteSearchWithGenesAndMeshterms() throws JSONException {
        logger.info("testExecuteSearchWithGenesAndMeshterms");
        JSONObject searchWithSearchQueryJsonObject = new JSONObject();
        searchWithSearchQueryJsonObject.put("searchQuery", genesAndMeshtermsSearchQueryJsonObject);
        HttpEntity<String> request = new HttpEntity<>(searchWithSearchQueryJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        String expectedResponse = "{\"searchQuery\":{\"symbol\":[\"leuD\"],\"geneId\":[\"1246505\"],\"meshTreeId\":[\"B01.050.500.308\"],\"name\":[\"Cnidaria\"],\"description\":[\"isopropylmalate isomerase small subunit\"],\"meshId\":[\"D003063\"]},\"results\":[{\"index\":0,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.0488}],\"hasMoreResults\":false}";
        assertEquals(expectedResponse, response.getBody());
    }

    @Test
    public void testExecuteSearchWithMultipartFileAndValidType() throws IOException {
        logger.info("testExecuteSearchWithMultipartFileAndValidType");
        Path testFile = Files.createTempFile("gene1Ids", ".csv");
        Files.write(testFile, "1246505".getBytes(StandardCharsets.UTF_8));
        logger.info("file getFileSystem " + testFile.getFileSystem().toString());
        logger.info("file getFileName " + testFile.getFileName().toString());
        logger.info("file getParent " + testFile.getParent().toString());
        FileSystemResource fileSystemResource = new FileSystemResource(testFile.toFile());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", fileSystemResource);
        map.add("type", "geneId");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, fileHeaders);
        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/api/search/upload"), HttpMethod.POST, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        String expectedResponse = "{\"searchQuery\":{\"symbol\":[],\"geneId\":[\"1246505\"],\"meshTreeId\":[],\"name\":[],\"description\":[],\"meshId\":[]},\"results\":[{\"index\":0,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D000818\",\"name\":\"Animals\",\"publicationCount\":13,\"pvalue\":0.001},{\"index\":1,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003063\",\"name\":\"Cnidaria\",\"publicationCount\":11,\"pvalue\":0.0488},{\"index\":2,\"geneId\":\"1246505\",\"description\":\"isopropylmalate isomerase small subunit\",\"symbol\":\"leuD\",\"meshId\":\"D003201\",\"name\":\"Computers\",\"publicationCount\":1,\"pvalue\":0.5433}],\"hasMoreResults\":false}";
        assertEquals(expectedResponse, response.getBody());
        Files.delete(testFile);
    }

    @Test
    public void testExecuteSearchWithEmptyMultipartFile() throws IOException {
        logger.info("testExecuteSearchWithEmptyMultipartFile");
        Path testFile = Files.createTempFile("gene2Ids", ".csv");
        FileSystemResource fileSystemResource = new FileSystemResource(testFile.toFile());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", fileSystemResource);
        map.add("type", "geneId");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, fileHeaders);
        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/api/search/upload"), HttpMethod.POST, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"File is empty.\"}", response.getBody());
        Files.delete(testFile);
    }

    @Test
    public void testExecuteSearchWithMultipartFileAndInvalidType() throws IOException {
        logger.info("testExecuteSearchWithMultipartFileAndInvalidType");
        Path testFile = Files.createTempFile("gene3Ids", ".csv");
        Files.write(testFile, "1246505".getBytes(StandardCharsets.UTF_8));
        FileSystemResource fileSystemResource = new FileSystemResource(testFile.toFile());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", fileSystemResource);
        map.add("type", "test");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, fileHeaders);
        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/api/search/upload"), HttpMethod.POST, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Invalid search query type: test.\"}", response.getBody());
        Files.delete(testFile);
    }

    @Test
    public void testExecuteSearchWithMultipartFileExceedingMaxNumberOfRecords() throws IOException {
        logger.info("testExecuteSearchWithMultipartFileAndInvalidType");
        int IMPORT_MAX_NUMBER_OF_RECORDS = 1000;
        Path testFile = Files.createTempFile("gene4Ids", ".csv");
        String geneId = "1246505" + System.lineSeparator();
        for (int i = 0; i < IMPORT_MAX_NUMBER_OF_RECORDS + 1; i++) {
            Files.write(testFile, geneId.getBytes(StandardCharsets.UTF_8), StandardOpenOption.APPEND);
        }
        FileSystemResource fileSystemResource = new FileSystemResource(testFile.toFile());
        MultiValueMap<String, Object> map = new LinkedMultiValueMap<>();
        map.add("file", fileSystemResource);
        map.add("type", "geneId");
        HttpEntity<MultiValueMap<String, Object>> request = new HttpEntity<>(map, fileHeaders);
        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/api/search/upload"), HttpMethod.POST, request, String.class);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Maximum number of records in import file exceeded 1000.\"}", response.getBody());
        Files.delete(testFile);
    }

    @Test
    public void testExecuteSearchWithMissingSearchQuery() {
        logger.info("testExecuteSearchWithMissingSearchQuery");
        JSONObject searchWithMissingSearchQueryJsonObject = new JSONObject();
        HttpEntity<String> request = new HttpEntity<>(searchWithMissingSearchQueryJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Missing search query.\"}", response.getBody());
    }

    @Test
    public void testExecuteSearchWithEmptySearchQuery() throws JSONException {
        logger.info("testExecuteSearchWithEmptySearchQuery");
        JSONObject searchWithEmptySearchQueryJsonObject = new JSONObject();
        searchWithEmptySearchQueryJsonObject.put("searchQuery", emptySearchQueryJsonObject);
        HttpEntity<String> request = new HttpEntity<>(searchWithEmptySearchQueryJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Search query cannot be empty.\"}", response.getBody());
    }

    @Test
    public void testExecuteSearchWithMissingSearchQueryType() throws JSONException {
        logger.info("testExecuteSearchWithMissingSearchQueryType");
        JSONObject searchWithMissingSearchQueryTypeJsonObject = new JSONObject();
        JSONObject missingSearchQueryTypeJsonObject = new JSONObject();
        searchWithMissingSearchQueryTypeJsonObject.put("searchQuery", missingSearchQueryTypeJsonObject);
        HttpEntity<String> request = new HttpEntity<>(searchWithMissingSearchQueryTypeJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Missing geneId.\"}", response.getBody());
    }

    @Test
    public void testExecuteSearchWithInvalidSearchQueryType() throws JSONException {
        logger.info("testExecuteSearchWithInvalidSearchQueryType");
        JSONObject searchWithInvalidSearchQueryTypeJsonObject = new JSONObject();
        JSONObject invalidSearchQueryTypeJsonObject = new JSONObject();
        invalidSearchQueryTypeJsonObject.put("geneId", "");
        searchWithInvalidSearchQueryTypeJsonObject.put("searchQuery", invalidSearchQueryTypeJsonObject);
        HttpEntity<String> request = new HttpEntity<>(searchWithInvalidSearchQueryTypeJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/search"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Invalid geneId.\"}", response.getBody());
    }

    @Test
    public void testBookmarkSearch() throws JSONException {
        logger.info("testBookmarkSearch");
        JSONObject searchWithValidSearchQueryAndValidSearchNameJsonObject = new JSONObject();
        searchWithValidSearchQueryAndValidSearchNameJsonObject.put("searchQuery", genesAndMeshtermsSearchQueryJsonObject);
        searchWithValidSearchQueryAndValidSearchNameJsonObject.put("searchName", "search name");
        HttpEntity<String> request = new HttpEntity<>(searchWithValidSearchQueryAndValidSearchNameJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/bookmarks"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CREATED);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"success\":\"Search bookmarked.\"}", response.getBody());
    }

    @Test
    public void testBookmarkSearchWithMissingSearchName() throws JSONException {
        logger.info("testBookmarkSearchWithMissingSearchName");
        JSONObject searchWithMissingSearchNameJsonObject = new JSONObject();
        searchWithMissingSearchNameJsonObject.put("searchQuery", genesAndMeshtermsSearchQueryJsonObject);
        HttpEntity<String> request = new HttpEntity<>(searchWithMissingSearchNameJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/bookmarks"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Missing search name.\"}", response.getBody());
    }

    @Test
    public void testBookmarkSearchWithBlankSearchName() throws JSONException {
        logger.info("testBookmarkSearchWithBlankSearchName");
        JSONObject searchWithBlankSearchNameJsonObject = new JSONObject();
        searchWithBlankSearchNameJsonObject.put("searchQuery", genesAndMeshtermsSearchQueryJsonObject);
        searchWithBlankSearchNameJsonObject.put("searchName", "");
        HttpEntity<String> request = new HttpEntity<>(searchWithBlankSearchNameJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/bookmarks"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Search name cannot be blank.\"}", response.getBody());
    }

    @Test
    public void testBookmarkSearchWithInvalidSearchQuery() throws JSONException {
        logger.info("testBookmarkSearchWithInvalidSearchQuery");
        JSONObject searchWithInvalidSearchQueryJsonObject = new JSONObject();
        searchWithInvalidSearchQueryJsonObject.put("searchQuery", "");
        HttpEntity<String> request = new HttpEntity<>(searchWithInvalidSearchQueryJsonObject.toString(), searchHeaders);
        logger.info("Request: \"" + request.toString() + "\"");
        ResponseEntity<String> response = testRestTemplate.postForEntity(createURLWithPort("/api/bookmarks"), request, String.class);
        logger.info("Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Invalid search query.\"}", response.getBody());
    }

    @Test
    public void testGetAllSearches() throws JSONException {
        logger.info("testGetAllSearches");
        JSONObject searchWithValidSearchQueryAndValidSearchNameJsonObject = new JSONObject();
        searchWithValidSearchQueryAndValidSearchNameJsonObject.put("searchQuery", genesAndMeshtermsSearchQueryJsonObject);
        searchWithValidSearchQueryAndValidSearchNameJsonObject.put("searchName", "search name");
        HttpEntity<String> bookmarkSearchRequest = new HttpEntity<>(searchWithValidSearchQueryAndValidSearchNameJsonObject.toString(), searchHeaders);
        logger.info("Bookmark Search Request: \"" + bookmarkSearchRequest.toString() + "\"");
        ResponseEntity<String> bookmarkSearchResponse = testRestTemplate.postForEntity(createURLWithPort("/api/bookmarks"), bookmarkSearchRequest, String.class);
        logger.info("Bookmark Search Response Status Code: \"" + bookmarkSearchResponse.getStatusCode().toString() + "\"");
        assertEquals(bookmarkSearchResponse.getStatusCode(), HttpStatus.CREATED);
        logger.info("Bookmark Search Response Body: \"" + bookmarkSearchResponse.getBody() + "\"");
        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/api/bookmarks"), HttpMethod.GET, new HttpEntity<>(searchHeaders), String.class);
        logger.info("Get All Searches Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Get All Searches Response Body: \"" + response.getBody() + "\"");
        assertThat(Objects.requireNonNull(response.getBody()).contains(searchWithValidSearchQueryAndValidSearchNameJsonObject.get("searchName").toString()));
    }

    @Test
    public void testDeleteSearch() throws JSONException {
        logger.info("testDeleteSearch");
        JSONObject searchWithValidSearchQueryAndValidSearchNameJsonObject = new JSONObject();
        searchWithValidSearchQueryAndValidSearchNameJsonObject.put("searchQuery", genesAndMeshtermsSearchQueryJsonObject);
        searchWithValidSearchQueryAndValidSearchNameJsonObject.put("searchName", "search name");
        HttpEntity<String> bookmarkSearchRequest = new HttpEntity<>(searchWithValidSearchQueryAndValidSearchNameJsonObject.toString(), searchHeaders);
        logger.info("Bookmark Search Request: \"" + bookmarkSearchRequest.toString() + "\"");
        ResponseEntity<String> bookmarkSearchResponse = testRestTemplate.postForEntity(createURLWithPort("/api/bookmarks"), bookmarkSearchRequest, String.class);
        logger.info("Bookmark Search Response Status Code: \"" + bookmarkSearchResponse.getStatusCode().toString() + "\"");
        assertEquals(bookmarkSearchResponse.getStatusCode(), HttpStatus.CREATED);
        logger.info("Bookmark Search Response Body: \"" + bookmarkSearchResponse.getBody() + "\"");
        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/api/bookmarks/1"), HttpMethod.DELETE, new HttpEntity<>(searchHeaders), String.class);
        logger.info("Delete Search Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.OK);
        logger.info("Delete Search Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"success\":\"Bookmark deleted.\"}", response.getBody());
    }

    @Test
    public void testDeleteInvalidBookmark() {
        logger.info("testDeleteInvalidBookmark");
        ResponseEntity<String> response = testRestTemplate.exchange(createURLWithPort("/api/bookmarks/0"), HttpMethod.DELETE, new HttpEntity<>(searchHeaders), String.class);
        logger.info("Delete Search Response Status Code: \"" + response.getStatusCode().toString() + "\"");
        assertEquals(response.getStatusCode(), HttpStatus.CONFLICT);
        logger.info("Delete Search Response Body: \"" + response.getBody() + "\"");
        assertEquals("{\"error\":\"Invalid bookmark.\"}", response.getBody());
    }

    @Test
    public void testUpdateMeshTreeIds() {
        List<String> meshTreeIds = new ArrayList<>();
        meshTreeIds.add("B01");
        meshTreeIds.add("B01.050");
        List<String> expectedMeshTreeIds = new ArrayList<>();
        expectedMeshTreeIds.add(".B01");
        expectedMeshTreeIds.add("B01.050");
        List<String> updatedMeshTreeIds = searchController.updateMeshTreeIds(meshTreeIds);
        assertEquals(expectedMeshTreeIds, updatedMeshTreeIds);
    }

}
