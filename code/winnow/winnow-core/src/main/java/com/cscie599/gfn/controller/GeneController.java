package com.cscie599.gfn.controller;


import com.cscie599.gfn.controller.exceptions.GeneNotFoundException;
import com.cscie599.gfn.entities.Gene;
import com.cscie599.gfn.entities.GeneMeshterm;
import com.cscie599.gfn.repository.GeneMeshtermRepository;
import com.cscie599.gfn.repository.GeneRepository;
import com.cscie599.gfn.views.GeneDetailCoOccuringGeneView;
import com.cscie599.gfn.views.GeneDetailMeshtermView;
import com.cscie599.gfn.views.GeneView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;
import java.math.BigInteger;

@RestController
@RequestMapping("/api")
@Api(value = "Genes", description = "Operations pertaining to genes in Gene Function Navigation")
public class GeneController {

    @Autowired
    GeneRepository repository;

    @Autowired
    GeneMeshtermRepository geneMeshtermRepository;

    @ApiOperation(value = "View a list of genes", response = List.class)
    @GetMapping("/genes")
    public List<GeneView> findAll() {
        List<Gene> genes = repository.findAll();
        List<GeneView> geneViews = new ArrayList<>();
        for (Gene gene : genes) {
            geneViews.add(new GeneView(gene.getGeneId(), gene.getDescription(), gene.getSymbol()));
        }
        return geneViews;
    }

    @ApiOperation(value = "Catch empty search")
    @GetMapping("/genes/search/")
    public List<GeneView> findNone() {
        return new ArrayList<>();
    }

    @ApiOperation(value = "Get genes whose id, symbol or description contain 'pattern'")
    @GetMapping("/genes/search/{pattern}")
    public List<GeneView> findAllContaining(@PathVariable String pattern) {
        List<Gene> genes = repository.findAllContaining(pattern.toLowerCase());
        List<GeneView> geneViews = new ArrayList<>();
        for (Gene gene : genes) {
            geneViews.add(new GeneView(gene.getGeneId(),gene.getDescription(), gene.getSymbol()));
        }
        return geneViews;
    }

    @ApiOperation(value = "View one gene")
    @GetMapping("/genes/{id:[0-9]+}")
    GeneView one(@PathVariable String id) {
        Gene gene = repository.findById(id)
                .orElseThrow(() -> new GeneNotFoundException(id));
        return new GeneView(gene.getGeneId(), gene.getDescription(), gene.getSymbol());
    }

    /*
     * When I click the link of a gene in the search results page,
     * POST to /genes
     * Request
     * {
     *     "geneId": geneId
     * }
     * Response
     * {
     *     "geneId": geneId,
     *     "symbol": geneSymbol,
     *     "description": geneDescription,
     *     "meshResults": [
     *         {
     *             "meshId": "meshId1",
     *             "name": "meshName1",
     *             "publicationCount": "publicationCount",
     *             "pvalue": "pValue"
     *         },
     *         {
     *             "meshId": "meshId2",
     *             "name": "meshName2",
     *             "publicationCount": "publicationCount",
     *             "pvalue": "pValue"
     *         },
     *     ],
     *     "geneResults": [
     *         {
     *             "geneId": "geneId1",
     *             "description": "geneDescription1",
     *             "symbol": "geneSymbol1",
     *             "publicationCount": "publicationCount"
     *         },
     *         {
     *             "geneId": "geneId2",
     *             "description": "geneDescription2",
     *             "symbol": "geneSymbol2",
     *             "publicationCount": "publicationCount"
     *         },
     *     ]
     * }
     */
    @ApiOperation(value = "View gene detail including MeSH terms enriched for gene.")
    @PostMapping("/genes")
    public ResponseEntity<?> getGeneDetail(@RequestBody Map<String, Object> body) {
        LinkedHashMap<String, Object> response = validate(body);
        if (response.containsKey("error")) {
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        String geneId = body.get("geneId").toString();
        Gene gene = repository.findById(geneId)
                .orElse(null);
        if (gene == null) {
            response.put("error", "Gene not found.");
            return new ResponseEntity<>(response, HttpStatus.CONFLICT);
        }
        List<GeneDetailMeshtermView> geneDetailMeshtermViews = new ArrayList<>();
        List<GeneMeshterm> geneMeshterms = geneMeshtermRepository.findByGeneIdOrderByPValue(geneId);
        for (GeneMeshterm geneMeshterm : geneMeshterms) {
            geneDetailMeshtermViews.add(new GeneDetailMeshtermView(
                    geneMeshterm.getMeshterm().getMeshId().trim(),
                    geneMeshterm.getMeshterm().getName().trim(),
                    geneMeshterm.getPublicationCount(),
                    geneMeshterm.getPValue()));
        }
        List<GeneDetailCoOccuringGeneView> geneDetailCoOccuringGeneViews = new ArrayList<>();
        List<Object[]> genePublications = repository.findCoOccurringGeneIdsAndCountsByGeneIdOrderByCounts(geneId);
        for (Object[] genePublication : genePublications) {
            String coOccurringGeneId = (String) genePublication[0];
            BigInteger count = (BigInteger) genePublication[1];
            Gene coOccurringGene = repository.findById(coOccurringGeneId)
                    .orElse(null);
            assert coOccurringGene != null;
            geneDetailCoOccuringGeneViews.add(new GeneDetailCoOccuringGeneView(
                    coOccurringGene.getGeneId().trim(),
                    coOccurringGene.getDescription().trim(),
                    coOccurringGene.getSymbol().trim(),
                    count));
        }
        response.put("geneId", geneId);
        response.put("symbol", gene.getSymbol().trim());
        response.put("description", gene.getDescription().trim());
        response.put("meshResults", geneDetailMeshtermViews);
        response.put("geneResults", geneDetailCoOccuringGeneViews);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    /*
     * Validate the search body request.
     */
    LinkedHashMap<String, Object> validate(Map<String, Object> body) {
        LinkedHashMap<String, Object> response = new LinkedHashMap<>();
        if (!(body.containsKey("geneId"))) {
            response.put("error", "Missing gene ID.");
        } else if (StringUtils.isBlank(body.get("geneId").toString())) {
            response.put("error", "Gene ID cannot be blank.");
        }
        return response;
    }

}
