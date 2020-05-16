package com.cscie599.gfn.controller;


import com.cscie599.gfn.entities.Gene;
import com.cscie599.gfn.entities.GeneMeshterm;
import com.cscie599.gfn.repository.GeneMeshtermRepository;
import com.cscie599.gfn.repository.GeneRepository;
import com.cscie599.gfn.views.GeneDetailCoOccuringGeneView;
import com.cscie599.gfn.views.GeneDetailMeshtermView;
import com.cscie599.gfn.views.GeneView;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.math.BigInteger;

@RestController
@RequestMapping("/api")
public class GeneController {

    @Value("${search.result.limit}")
    private int searchResultLimit;

    @Autowired
    GeneRepository geneRepository;

    @Autowired
    GeneMeshtermRepository geneMeshtermRepository;

    /**
     * Catches empty search.
     *
     * @return empty ArrayList
     */
    @GetMapping("/genes/search/")
    public List<GeneView> findNone() {
        return new ArrayList<>();
    }

    /**
     * Gets gene IDs, symbols, or descriptions matching pattern.
     *
     * @param pattern Pattern for matching gene
     * @return List of gene views for genes matching the pattern
     */
    @GetMapping("/genes/search/{pattern}")
    public List<GeneView> findAllContaining(@PathVariable String pattern) {
        List<Gene> genes = geneRepository.findAllContaining(pattern.toLowerCase());
        List<GeneView> geneViews = new ArrayList<>();
        for (Gene gene : genes) {
            geneViews.add(new GeneView(gene.getGeneId(),gene.getDescription(), gene.getSymbol()));
        }
        return geneViews;
    }

    /**
     * Retrieves gene detail and MeSH terms enriched for publications and co-occurring genes in publications.
     *
     * @param body Request body containing gene ID
     * @return ResponseEntity containing gene detail, MeSH terms enriched for publications, and co-occurring genes in publications
     */
    @PostMapping("/genes")
    public ResponseEntity<?> getGeneDetail(@RequestBody Map<String, Object> body) {
        // Validate the request body before proceeding with the request
        LinkedHashMap<String, Object> response = validate(body);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        // Retrieve the gene from the db if it exists
        String geneId = body.get("geneId").toString();
        Gene gene = geneRepository.findById(geneId)
                .orElse(null);
        if (gene == null) {
            response.put("error", "Gene not found.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        // Get all the MeSH terms, publication counts, and p-values associated with the gene
        List<GeneDetailMeshtermView> geneDetailMeshtermViews = new ArrayList<>();
        List<GeneMeshterm> geneMeshterms = geneMeshtermRepository.findByGeneIdOrderByPValue(geneId, 0, searchResultLimit);
        for (GeneMeshterm geneMeshterm : geneMeshterms) {
            geneDetailMeshtermViews.add(new GeneDetailMeshtermView(
                    geneMeshterm.getMeshterm().getMeshId().trim(),
                    geneMeshterm.getMeshterm().getName().trim(),
                    geneMeshterm.getPublicationCount(),
                    geneMeshterm.getPValue()));
        }
        // Get all the genes that co-occur with the gene and publication counts
        List<GeneDetailCoOccuringGeneView> geneDetailCoOccuringGeneViews = new ArrayList<>();
        List<Object[]> genePublications = geneRepository.findCoOccurringGeneIdsAndCountsByGeneIdOrderByCounts(geneId);
        for (Object[] genePublication : genePublications) {
            String coOccurringGeneId = (String) genePublication[0];
            BigInteger count = (BigInteger) genePublication[1];
            geneRepository.findById(coOccurringGeneId).ifPresent(coOccurringGene -> geneDetailCoOccuringGeneViews.add(new GeneDetailCoOccuringGeneView(
                    coOccurringGene.getGeneId().trim(),
                    coOccurringGene.getDescription().trim(),
                    coOccurringGene.getSymbol().trim(),
                    count)));
        }
        // Return the detail of the gene along with the MeSH terms and co-occurring gene results
        response.put("geneId", geneId);
        response.put("symbol", gene.getSymbol().trim());
        response.put("description", gene.getDescription().trim());
        response.put("meshResults", geneDetailMeshtermViews);
        response.put("geneResults", geneDetailCoOccuringGeneViews);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /**
     * Validates the body request for getGeneDetail.
     *
     * @param body Request body
     * @return Response containing error if any
     */
    private LinkedHashMap<String, Object> validate(Map<String, Object> body) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        if (!(body.containsKey("geneId"))) {
            response.put("error", "Missing gene ID.");
        } else if (body.get("geneId") == null) {
            response.put("error", "Gene ID cannot be null.");
        } else if (StringUtils.isBlank(body.get("geneId").toString())) {
            response.put("error", "Gene ID cannot be blank.");
        }
        return response;
    }

}
