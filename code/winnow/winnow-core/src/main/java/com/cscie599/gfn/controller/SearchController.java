package com.cscie599.gfn.controller;


import com.cscie599.gfn.entities.GeneMeshterm;
import com.cscie599.gfn.entities.Search;
import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.repository.GeneMeshtermRepository;
import com.cscie599.gfn.repository.GeneRepository;
import com.cscie599.gfn.repository.MeshtermRepository;
import com.cscie599.gfn.repository.SearchRepository;
import com.cscie599.gfn.repository.UserRepository;
import com.cscie599.gfn.views.GeneMeshtermView;
import com.cscie599.gfn.views.SearchView;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class SearchController {

    private static final Log logger = LogFactory.getLog(SearchController.class);

    @Value("${search.result.limit}")
    private int searchResultLimit;

    @Autowired
    SearchRepository searchRepository;

    @Autowired
    GeneMeshtermRepository geneMeshtermRepository;

    @Autowired
    GeneRepository geneRepository;

    @Autowired
    MeshtermRepository meshtermRepository;

    @Autowired
    UserRepository userRepository;

    private static String[] searchQueryTypes = {"geneId", "symbol", "description", "meshTreeId", "meshId", "name"};

    /**
     * Gets a user's list of bookmarked searches.
     *
     * @param authentication Authentication token
     * @return ResponseEntity containing search views
     */
    @GetMapping("/bookmarks")
    public ResponseEntity<?> getAllSearches(Authentication authentication) {
        String userEmail = authentication.getPrincipal().toString();
        List<Search> searches = searchRepository.findByCreatedByOrderBySearchId(userEmail);
        List<SearchView> searchViews = new ArrayList<>();
        for (Search search : searches) {
            searchViews.add(new SearchView(
                    search.getSearchId(),
                    search.getSearchName().trim(),
                    search.getSearchQuery(),
                    search.getCreatedDate(),
                    search.getUpdatedAt()));
        }
        return new ResponseEntity<>(searchViews, HttpStatus.OK);
    }

    /**
     * Bookmark a search. searchQuery is a JSON object containing search terms.
     *
     * @param body           RequestBody containing search query and search name
     * @param authentication Authentication token
     * @return ResponseEntity containing success or error
     */
    @PostMapping("/bookmarks")
    public ResponseEntity<?> bookmarkSearch(@RequestBody Map<String, Object> body, Authentication authentication) {
        LinkedHashMap<String, Object> response = validate(body);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        if (!(body.containsKey("searchName"))) {
            response.put("error", "Missing search name.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        } else if (StringUtils.isBlank(body.get("searchName").toString())) {
            response.put("error", "Search name cannot be blank.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        String userEmail = authentication.getPrincipal().toString();
        User user = userRepository.findByUserEmail(userEmail);
        Search search = new Search();
        search.setCreatedBy(user);
        search.setSearchName(body.get("searchName").toString());
        search.setSearchQuery((HashMap<String, Object>) body.get("searchQuery"));
        search.setCreatedDate(new Date());
        search.setUpdatedAt(new Date());
        searchRepository.save(search);
        response.put("success", "Search bookmarked.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /**
     * Delete a bookmarked search.
     *
     * @param id             Bookmark ID
     * @param authentication Authentication token
     * @return ResponseEntity containing success or error
     */
    @DeleteMapping("/bookmarks/{id}")
    public ResponseEntity<?> deleteSearch(@PathVariable Long id, Authentication authentication) {
        HashMap<String, Object> response = new HashMap<>();
        String userEmail = authentication.getPrincipal().toString();
        Search search = searchRepository.findByCreatedByAndSearchId(userEmail, id);
        if (search == null) {
            response.put("error", "Invalid bookmark.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        searchRepository.delete(search);
        response.put("success", "Bookmark deleted.");
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Retrieves search results given a search query. Search query includes the following types:
     * gene ids, gene symbols, gene descriptions, MeSH term ids, MeSH term names, and MeSH term tree ids.
     *
     * @param body Request body containing search query
     * @return ResponseEntity containing search query, search results, and has more search results, or error
     */
    @PostMapping("/search")
    public ResponseEntity<?> executeSearch(@RequestBody Map<String, Object> body) {
        LinkedHashMap<String, Object> response = validate(body);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        HashMap<String, Object> searchQuery = (HashMap<String, Object>) body.get("searchQuery");
        int queryOffset = getQueryOffset(body);
        updateResponse(response, searchQuery, queryOffset);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Uploads a CSV list of genes or MeSH terms along with a search query type. Search query type includes:
     * gene ids, gene symbols, gene descriptions, MeSH term ids, MeSH term names, and MeSH term tree ids.
     *
     * @param file CSV file of genes or MeSH terms
     * @param type Search query type geneId|symbol|description|meshTreeId|meshId|name
     * @return ResponseEntity containing search query, search results, and has more search results, or error
     */
    @PostMapping("/search/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile file, @RequestParam("type") String type) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        if (!file.isEmpty()) {
            try {
                byte[] bytes = file.getBytes();
                String string = new String(bytes, StandardCharsets.UTF_8.name());
                List<String> records = new ArrayList<>(Arrays.asList(string.split("\\r?\\n")));
                int IMPORT_MAX_NUMBER_OF_RECORDS = 1000;
                if (records.size() > IMPORT_MAX_NUMBER_OF_RECORDS) {
                    response.put("error", "Maximum number of records in import file exceeded " + IMPORT_MAX_NUMBER_OF_RECORDS + ".");
                    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
                }
                HashMap<String, Object> searchQuery = new HashMap<>();
                List<String> emptyList = new ArrayList<>();
                for (String searchQueryType : searchQueryTypes) {
                    if (searchQueryType.equals(type)) {
                        searchQuery.put(searchQueryType, records);
                    } else {
                        searchQuery.put(searchQueryType, emptyList);
                    }
                }
                if (!(searchQuery.containsKey(type))) {
                    response.put("error", "Invalid search query type: " + type + ".");
                    return new ResponseEntity<>(response, HttpStatus.CONFLICT);
                } else {
                    updateResponse(response, searchQuery, 0);
                    return new ResponseEntity<>(response, HttpStatus.OK);
                }
            } catch (IOException e) {
                response.put("error", e.getMessage());
                return new ResponseEntity<>(response, HttpStatus.CONFLICT);
            }
        } else {
            response.put("error", "File is empty.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
    }

    /**
     * Checks if the requestBody has the field <b>queryOffset</b>. If it exists returns its value otherwise returns 0;
     *
     * @param body Request body
     * @return Query offset if set else 0
     */
    private int getQueryOffset(Map<String, Object> body) {
        try {
            return (Integer) body.getOrDefault("queryOffset", 0);
        } catch (Exception ex) {
            logger.warn("Input queryOffset needs to be a long type " + body.get("queryOffset"));
        }
        return 0;
    }

    /**
     * Validates the body request for searches.
     *
     * @param body Request body
     * @return Response containing error if any
     */
    private LinkedHashMap<String, Object> validate(Map<String, Object> body) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        if (!(body.containsKey("searchQuery"))) {
            response.put("error", "Missing search query.");
            return response;
        } else if (!(body.get("searchQuery") instanceof Map)) {
            response.put("error", "Invalid search query.");
            return response;
        }
        HashMap<String, Object> searchQuery = (HashMap<String, Object>) body.get("searchQuery");
        int count = 0;
        for (String searchQueryType : searchQueryTypes) {
            if (!(searchQuery.containsKey(searchQueryType))) {
                response.put("error", "Missing " + searchQueryType + ".");
                return response;
            }
            if (!(searchQuery.get(searchQueryType) instanceof List)) {
                response.put("error", "Invalid " + searchQueryType + ".");
                return response;
            } else {
                if (((List) (searchQuery.get(searchQueryType))).isEmpty()) {
                    count++;
                }
            }
        }
        if (count == searchQueryTypes.length) {
            response.put("error", "Search query cannot be empty.");
            return response;
        }
        return response;
    }

    /**
     * Updates the response with the search query, search results, and has more search results
     *
     * @param response    Response containing search query, search results, and has more search results
     * @param searchQuery Search query
     * @param offset      Offset for search results
     */
    private void updateResponse(LinkedHashMap<String, Object> response, HashMap<String, Object> searchQuery, int offset) {
        List<GeneMeshterm> geneMeshterms = getGeneMeshterms(searchQuery, offset);
        List<GeneMeshtermView> geneMeshtermViews = new ArrayList<>();
        long i = offset;
        long endIndex = offset + searchResultLimit;
        for (GeneMeshterm geneMeshterm : geneMeshterms) {
            if (i < endIndex) {
                geneMeshtermViews.add(new GeneMeshtermView(
                        i++,
                        geneMeshterm.getGene().getGeneId().trim(),
                        geneMeshterm.getGene().getDescription().trim(),
                        geneMeshterm.getGene().getSymbol().trim(),
                        geneMeshterm.getMeshterm().getMeshId().trim(),
                        geneMeshterm.getMeshterm().getName().trim(),
                        geneMeshterm.getPublicationCount(),
                        geneMeshterm.getPValue()));
            }
        }
        response.put("searchQuery", searchQuery);
        response.put("results", geneMeshtermViews);
        response.put("hasMoreResults", geneMeshterms.size() > searchResultLimit ? true : false);
    }

    /**
     * Gets the list of gene to MeSH term pairs.
     *
     * @param searchQuery Search query
     * @param offset      Offset for search results
     * @return List of Gene to MeSH term pairs
     */
    private List<GeneMeshterm> getGeneMeshterms(HashMap<String, Object> searchQuery, long offset) {
        List<String> geneIds = (ArrayList) searchQuery.get("geneId");
        List<String> symbols = (ArrayList) searchQuery.get("symbol");
        List<String> updatedSymbols = symbols.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        List<String> descriptions = (ArrayList) searchQuery.get("description");
        List<String> updatedDescriptions = descriptions.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        List<String> meshIds = (ArrayList) searchQuery.get("meshId");
        List<String> names = (ArrayList) searchQuery.get("name");
        List<String> updatedNames = names.stream()
                .map(String::toLowerCase)
                .collect(Collectors.toList());
        List<String> meshTreeIds = (ArrayList) searchQuery.get("meshTreeId");
        List<String> updatedMeshTreeIds = updateMeshTreeIds(meshTreeIds);
        List<String> updatedGeneIds = geneRepository.findGeneIdsByGeneIdsOrSymbolsOrDescriptions(geneIds, updatedSymbols, updatedDescriptions);
        List<String> updatedMeshIds = meshtermRepository.findMeshIdsByMeshIdsOrNamesOrMeshTreeIds(meshIds, updatedNames, updatedMeshTreeIds);
        List<GeneMeshterm> geneMeshterms = new ArrayList<>();
        // We always query for 1 additional record then what gets returned as the response to check if
        // there are any more records after the current batch
        int recordsToQuery = searchResultLimit + 1;
        if (geneIds.isEmpty() && symbols.isEmpty() && descriptions.isEmpty() && !(updatedMeshIds.isEmpty())) {
            geneMeshterms = geneMeshtermRepository.findByMeshIdsOrderByPValue(updatedMeshIds, offset, recordsToQuery);
        } else if (meshIds.isEmpty() && names.isEmpty() && meshTreeIds.isEmpty() && !(updatedGeneIds.isEmpty())) {
            geneMeshterms = geneMeshtermRepository.findByGeneIdsOrderByPValue(updatedGeneIds, offset, recordsToQuery);
        } else if (!(updatedGeneIds.isEmpty()) && !(updatedMeshIds.isEmpty())) {
            geneMeshterms = geneMeshtermRepository.findByGeneIdsAndMeshIdsOrderByPValue(updatedGeneIds, updatedMeshIds, offset, recordsToQuery);
        }
        return geneMeshterms;
    }

    /**
     * Prepends a list of MeSH tree IDs for records that have empty parent tree IDs with a period
     * (for example, "B50" -> ".B50") for findMeshIdsByMeshIdsOrNamesOrMeshTreeIds method in MeshtermRepository.
     *
     * @param meshTreeIds List of MeSH tree IDs
     * @return Updated list of MeSH tree IDs
     */
    private List<String> updateMeshTreeIds(List<String> meshTreeIds) {
        List<String> updatedMeshTreeIds = new ArrayList<>();
        for (String s : meshTreeIds) {
            String[] treeId = s.split("\\.");
            String treeParentId = "";
            String treeNodeId = treeId[treeId.length - 1];
            if (treeId.length >= 2) {
                for (int i = 0; i < treeId.length - 2; i++) {
                    treeParentId += treeId[i] + ".";
                }
                treeParentId += treeId[treeId.length - 2];
            }
            if (treeId.length == 1) {
                treeParentId = "";
            }
            updatedMeshTreeIds.add(treeParentId + "." + treeNodeId);
        }
        return updatedMeshTreeIds;
    }

}
