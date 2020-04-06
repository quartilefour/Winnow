package com.cscie599.gfn.controller;


import com.cscie599.gfn.entities.GeneMeshterm;
import com.cscie599.gfn.entities.Search;
import com.cscie599.gfn.entities.User;
import com.cscie599.gfn.repository.GeneMeshtermRepository;
import com.cscie599.gfn.repository.SearchRepository;
import com.cscie599.gfn.repository.UserRepository;
import com.cscie599.gfn.views.GeneMeshtermView;
import com.cscie599.gfn.views.SearchView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import org.apache.commons.lang3.StringUtils;

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
                    search.getQueryType().trim(),
                    search.getQueryFormat().trim(),
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
     *     "searchQuery": [term1, term2, term3,],
     *     "queryType": "mesh|gene"
     *     "queryFormat": "meshid|meshtreeid|meshname|geneid|genesymbol"
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
        String searchName = body.get("searchName").toString();
        Object searchQueryObject = body.get("searchQuery");
        List<String> searchQueryList = Arrays.asList(body.get("searchQuery").toString()
                .substring(1, body.get("searchQuery").toString().length() - 1).split("\\s*,\\s*"));
        String queryType = body.get("queryType").toString();
        String queryFormat = body.get("queryFormat").toString();
        search.setCreatedBy(user);
        search.setSearchName(searchName);
        search.setSearchQuery(searchQueryList);
        search.setQueryType(queryType);
        search.setQueryFormat(queryFormat);
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
     * When I click the search button after entering my mesh terms in MeSH 2 Gene or genes in Gene 2 MeSH,
     * POST to /search
     * Request
     * {
     *     "searchQuery": [term1, term2, term3,],
     *     "queryType": "mesh|gene"
     *     "queryFormat": "meshid|meshtreeid|meshname|geneid|genesymbol"
     * }
     * Response
     * {
     *      "searchQuery": [term1, term2, term3,],
     *      "queryType": "mesh|gene",
     *      "queryFormat": "meshid|meshtreeid|meshname|geneid|genesymbol"
     *      "results": [
     *          {
     *              "geneId": "geneId1",
     *              "description": "geneDescription1",
     *              "symbol": "geneSymbol1",
     *              "meshId": "meshId1",
     *              "name": "meshName1",
     *              "publicationCount": "publicationCount",
     *              "pvalue": "pValue"
     *          },
     *          {
     *              "geneId": "geneId2",
     *              "description": "geneDescription2",
     *              "symbol": "geneSymbol2",
     *              "meshId": "meshId2",
     *              "name": "meshName2",
     *              "publicationCount": "publicationCount",
     *              "pvalue": "pValue"
     *          },
     *      ]
     * }
     */
    @ApiOperation(value = "Search results.")
    @PostMapping("/search")
    public ResponseEntity<?> executeSearch(@RequestBody Map<String, Object> body) {
        LinkedHashMap<String, Object> response = validate(body);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        Object searchQueryObject = body.get("searchQuery");
        List<String> searchQueryList = Arrays.asList(body.get("searchQuery").toString()
                .substring(1, body.get("searchQuery").toString().length() - 1).split("\\s*,\\s*"));
        String queryType = body.get("queryType").toString();
        String queryFormat = body.get("queryFormat").toString();
        List<GeneMeshtermView> geneMeshtermViews = new ArrayList<>();
        List<GeneMeshterm> geneMeshterms = new ArrayList<>();
        switch (queryFormat) {
            case "meshid":
                geneMeshterms = geneMeshtermRepository.findByMeshIdsOrderByPValue(searchQueryList);
                break;
            case "meshtreeid":
                // Update the search query list so that the mesh tree records with empty parent ids have "." before it
                // (for example, "B50" -> ".B50") for findByMeshTreeIds() method
                List<String> updatedSearchQueryList = new ArrayList<String>();
                for (String s : searchQueryList) {
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
                    updatedSearchQueryList.add(treeParentId + "." + treeNodeId);
                }
                geneMeshterms = geneMeshtermRepository.findByMeshTreeIdsOrderByPValue(updatedSearchQueryList);
                break;
            case "meshname":
                geneMeshterms = geneMeshtermRepository.findByMeshNamesOrderByPValue(searchQueryList);
                break;
            case "geneid":
                geneMeshterms = geneMeshtermRepository.findByGeneIdsOrderByPValue(searchQueryList);
                break;
            case "genesymbol":
                geneMeshterms = geneMeshtermRepository.findByGeneSymbolsOrderByPValue(searchQueryList);
                break;
            default:
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
        response.put("searchQuery", searchQueryObject);
        response.put("queryType", queryType);
        response.put("queryFormat", queryFormat);
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
        } else if (!(body.containsKey("queryType"))) {
            response.put("error", "Missing query type.");
        } else if (!(body.containsKey("queryFormat"))) {
            response.put("error", "Missing query format.");
        } else if (StringUtils.isBlank(body.get("queryType").toString())) {
            response.put("error", "Query type cannot be blank.");
        } else if (StringUtils.isBlank(body.get("queryFormat").toString())) {
            response.put("error", "Query format cannot be blank.");
        } else if (!(body.get("searchQuery") instanceof List<?>)) {
            response.put("error", "Invalid search query.");
        } else if (body.get("searchQuery").toString() == "[]") {
            response.put("error", "Search query cannot be empty.");
        } else if (!body.get("queryType").equals("gene") && !body.get("queryType").equals("mesh")) {
            response.put("error", "Invalid query type.");
        } else if (!body.get("queryFormat").equals("meshid") && !body.get("queryFormat").equals("meshtreeid")
                && !body.get("queryFormat").equals("meshname") && !body.get("queryFormat").equals("geneid")
                && !body.get("queryFormat").equals("genesymbol")) {
            response.put("error", "Invalid query format.");
        }
        return response;
    }

}
