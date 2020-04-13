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
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api")
@Api(value = "Searches", description = "Operations pertaining to searches in Gene Function Navigation")
public class SearchController {

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

    /*
     * When I click the bookmark tab,
     * GET to /bookmarks
     */
    @ApiOperation(value = "View user's bookmarks.")
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

    /*
     * When I click the bookmark button to bookmark the search results,
     * POST to /bookmarks
     * Request
     * {
     *     "searchQuery": {
     *         "geneId": ["geneId1", "geneId2",],
     *         "description": ["geneDescription1", "geneDescription2",],
     *         "symbol": ["geneSymbol1", "geneSymbol2",],
     *         "meshId": ["meshId1", "meshId2",],
     *         "meshTreeId": ["meshTreeId1", "meshTreeId2",],
     *         "name": ["meshName1", "meshName2",]
     *     }
     *     "searchName": "search name"
     * }
     */
    @ApiOperation(value = "Bookmark a search.")
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
        search.setSearchQuery((HashMap) body.get("searchQuery"));
        search.setCreatedDate(new Date());
        search.setUpdatedAt(new Date());
        Search savedSearch = searchRepository.save(search);
        response.put("success", "Search bookmarked.");
        return new ResponseEntity<>(response, HttpStatus.CREATED);
    }

    /*
     * When I click the delete button for a bookmark,
     * DELETE to /bookmarks/{id}
     */
    @ApiOperation(value = "Delete a bookmark.")
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

    /*
     * When I click the search button after entering my genes and/or MeSH terms,
     * POST to /search
     * Request
     * {
     *     "searchQuery": {
     *         "geneId": ["geneId1", "geneId2",],
     *         "description": ["geneDescription1", "geneDescription2",],
     *         "symbol": ["geneSymbol1", "geneSymbol2",],
     *         "meshId": ["meshId1", "meshId2",],
     *         "meshTreeId": ["meshTreeId1", "meshTreeId2",],
     *         "name": ["meshName1", "meshName2",]
     *     }
     * }
     * Response
     * {
     *     "searchQuery": {
     *         "geneId": ["geneId1", "geneId2",],
     *         "description": ["geneDescription1", "geneDescription2",],
     *         "symbol": ["geneSymbol1", "geneSymbol2",],
     *         "meshId": ["meshId1", "meshId2",],
     *         "meshTreeId": ["meshTreeId1", "meshTreeId2",],
     *         "name": ["meshName1", "meshName2",]
     *     }
     *     "results": [
     *         {
     *             "geneId": "geneId1",
     *             "description": "geneDescription1",
     *             "symbol": "geneSymbol1",
     *             "meshId": "meshId1",
     *             "name": "meshName1",
     *             "publicationCount": "publicationCount",
     *             "pvalue": "pValue"
     *         },
     *         {
     *             "geneId": "geneId2",
     *             "description": "geneDescription2",
     *             "symbol": "geneSymbol2",
     *             "meshId": "meshId2",
     *             "name": "meshName2",
     *             "publicationCount": "publicationCount",
     *             "pvalue": "pValue"
     *         },
     *     ]
     * }
     */
    @ApiOperation(value = "Search results.")
    @PostMapping("/search")
    public ResponseEntity<?> executeSearch(@RequestBody Map<String, Object> body) {
        LinkedHashMap<String, Object> response = validate(body);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        HashMap<String, Object> searchQuery = (HashMap) body.get("searchQuery");
        List<String> geneIds = Arrays.asList(searchQuery.get("geneId").toString()
                .substring(1, searchQuery.get("geneId").toString().length() - 1).split("\\s*,\\s*"));
        List<String> symbols = Arrays.asList(searchQuery.get("symbol").toString()
                .substring(1, searchQuery.get("symbol").toString().length() - 1).split("\\s*,\\s*"));
        List<String> descriptions = Arrays.asList(searchQuery.get("description").toString()
                .substring(1, searchQuery.get("description").toString().length() - 1).split("\\s*,\\s*"));
        List<String> meshIds = Arrays.asList(searchQuery.get("meshId").toString()
                .substring(1, searchQuery.get("meshId").toString().length() - 1).split("\\s*,\\s*"));
        List<String> names = Arrays.asList(searchQuery.get("name").toString()
                .substring(1, searchQuery.get("name").toString().length() - 1).split("\\s*,\\s*"));
        List<String> meshTreeIds = Arrays.asList(searchQuery.get("meshTreeId").toString()
                .substring(1, searchQuery.get("meshTreeId").toString().length() - 1).split("\\s*,\\s*"));
        List<String> updatedMeshTreeIds = updateMeshTreeIds(meshTreeIds);
        List<String> updatedGeneIds = geneRepository.findGeneIdsByGeneIdsOrSymbolsOrDescriptions(geneIds, symbols, descriptions);
        List<String> updatedMeshIds = meshtermRepository.findMeshIdsByMeshIdsOrNamesOrMeshTreeIds(meshIds, names, updatedMeshTreeIds);
        List<GeneMeshtermView> geneMeshtermViews = new ArrayList<>();
        List<GeneMeshterm> geneMeshterms = new ArrayList<>();
        if (updatedGeneIds.toString() == "[]" && !(updatedMeshIds.toString() == "[]")) {
            geneMeshterms = geneMeshtermRepository.findByMeshIdsOrderByPValue(updatedMeshIds);
        } else if (!(updatedGeneIds.toString() == "[]") && updatedMeshIds.toString() == "[]") {
            geneMeshterms = geneMeshtermRepository.findByGeneIdsOrderByPValue(updatedGeneIds);
        } else if (!(updatedGeneIds.toString() == "[]") && !(updatedMeshIds.toString() == "[]")) {
            geneMeshterms = geneMeshtermRepository.findByGeneIdsAndMeshIdsOrderByPValue(updatedGeneIds, updatedMeshIds);
        }
        for (GeneMeshterm geneMeshterm : geneMeshterms) {
            geneMeshtermViews.add(new GeneMeshtermView(
                    geneMeshterm.getGene().getGeneId().trim(),
                    geneMeshterm.getGene().getDescription().trim(),
                    geneMeshterm.getGene().getSymbol().trim(),
                    geneMeshterm.getMeshterm().getMeshId().trim(),
                    geneMeshterm.getMeshterm().getName().trim(),
                    geneMeshterm.getPublicationCount(),
                    geneMeshterm.getPValue()));
        }
        response.put("searchQuery", searchQuery);
        response.put("results", geneMeshtermViews);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
     * Validate the search body request.
     */
    LinkedHashMap<String, Object> validate(Map<String, Object> body) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        if (!(body.containsKey("searchQuery"))) {
            response.put("error", "Missing search query.");
            return response;
        } else if (!(body.get("searchQuery") instanceof HashMap)) {
            response.put("error", "Invalid search query.");
            return response;
        }
        HashMap<String, Object> searchQuery = (HashMap) body.get("searchQuery");
        if (!(searchQuery.containsKey("geneId"))) {
            response.put("error", "Missing geneId.");
        } else if (!(searchQuery.containsKey("symbol"))) {
            response.put("error", "Missing symbol.");
        } else if (!(searchQuery.containsKey("description"))) {
            response.put("error", "Missing description.");
        } else if (!(searchQuery.containsKey("meshTreeId"))) {
            response.put("error", "Missing meshTreeId.");
        } else if (!(searchQuery.containsKey("meshId"))) {
            response.put("error", "Missing meshId.");
        } else if (!(searchQuery.containsKey("name"))) {
            response.put("error", "Missing name.");
        } else if (searchQuery.get("geneId").toString() == "[]"
                && searchQuery.get("symbol").toString() == "[]"
                && searchQuery.get("description").toString() == "[]"
                && searchQuery.get("meshTreeId").toString() == "[]"
                && searchQuery.get("meshId").toString() == "[]"
                && searchQuery.get("name").toString() == "[]") {
            response.put("error", "Search query cannot be empty.");
        }
        return response;
    }

    /*
     * Update the search query list so that the mesh tree records with empty parent ids have "." before it
     * (for example, "B50" -> ".B50") for findMeshIdsByMeshIdsOrNamesOrMeshTreeIds() method in MeshtermRepository.
     */
    List<String> updateMeshTreeIds(List<String> meshTreeIds) {
        List<String> updatedMeshTreeIds = new ArrayList<>();
        for (String s : meshTreeIds) {
            String[] treeId = s.toString().split("\\.");
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
