package com.cscie599.gfn.controller;


import com.cscie599.gfn.controller.exceptions.GeneNotFoundException;
import com.cscie599.gfn.entities.Gene;
import com.cscie599.gfn.repository.GeneRepository;
import com.cscie599.gfn.views.GeneView;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api")
@Api(value = "Genes", description = "Operations pertaining to genes in Gene Function Navigation")
public class GeneController {

    @Autowired
    GeneRepository repository;

    @ApiOperation(value = "View a list of genes", response = List.class)
    @GetMapping("/genes")
    public List<GeneView> findAll(){
        List<Gene> genes = repository.findAll();
        List<GeneView> geneViews = new ArrayList<>();
        for (Gene gene : genes) {
            geneViews.add(new GeneView(gene.getGeneId(),gene.getDescription()));
        }
        return geneViews;
    }

    @ApiOperation(value = "View one gene")
    @GetMapping("/genes/{id}")
    GeneView one(@PathVariable String id) {
        Gene gene = repository.findById(id)
                .orElseThrow(() -> new GeneNotFoundException(id));
        return new GeneView(gene.getGeneId(), gene.getDescription());
    }
}
