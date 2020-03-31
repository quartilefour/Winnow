package com.cscie599.gfn.controller;


import com.cscie599.gfn.controller.exceptions.MeshtermCategoryNotFoundException;
import com.cscie599.gfn.controller.exceptions.MeshtermNotFoundException;
import com.cscie599.gfn.entities.Meshterm;
import com.cscie599.gfn.entities.MeshtermCategory;
import com.cscie599.gfn.entities.MeshtermTree;
import com.cscie599.gfn.repository.MeshtermCategoryRepository;
import com.cscie599.gfn.repository.MeshtermRepository;
import com.cscie599.gfn.repository.MeshtermTreeRepository;
import com.cscie599.gfn.views.MeshtermCategoryView;
import com.cscie599.gfn.views.MeshtermTreeView;
import com.cscie599.gfn.views.MeshtermView;
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
@Api(value = "Meshterms", description = "Operations pertaining to MeSH terms in Gene Function Navigation")
public class MeshtermController {

    @Autowired
    MeshtermRepository repository;

    @Autowired
    MeshtermCategoryRepository categoryRepository;

    @Autowired
    MeshtermTreeRepository treeRepository;

    @ApiOperation(value = "View a list of mesh terms", response = List.class)
    @GetMapping("/meshterms")
    public List<MeshtermView> findAll(){
        List<Meshterm> meshterms = repository.findAll();
        List<MeshtermView> meshtermViews = new ArrayList<>();
        for (Meshterm meshterm : meshterms) {
            meshtermViews.add(new MeshtermView(meshterm.getMeshId(), meshterm.getName()));
        }
        return meshtermViews;
    }

    @ApiOperation(value = "View one mesh term")
    @GetMapping("/meshterms/{id}")
    MeshtermView one(@PathVariable String id) {
        Meshterm meshterm = repository.findById(id)
                .orElseThrow(() -> new MeshtermNotFoundException(id));
        return new MeshtermView(meshterm.getMeshId(), meshterm.getName());
    }

    @ApiOperation(value = "View a list of mesh term categories", response = List.class)
    @GetMapping("/meshterms/category")
    public List<MeshtermCategoryView> findAllCategories(){
        List<MeshtermCategory> meshtermCategories = categoryRepository.findAll();
        List<MeshtermCategoryView> meshtermCategoryViews = new ArrayList<>();
        for (MeshtermCategory meshtermCategory : meshtermCategories) {
            meshtermCategoryViews.add(new MeshtermCategoryView(meshtermCategory.getCategoryId(), meshtermCategory.getName()));
        }
        return meshtermCategoryViews;
    }

    @ApiOperation(value = "View one mesh term category")
    @GetMapping("/meshterms/category/{id}")
    MeshtermCategoryView oneCategory(@PathVariable String id) {
        MeshtermCategory meshtermCategory = categoryRepository.findById(id)
                .orElseThrow(() -> new MeshtermCategoryNotFoundException(id));
        return new MeshtermCategoryView(meshtermCategory.getCategoryId(), meshtermCategory.getName());
    }

    @ApiOperation(value = "View a list of mesh term trees", response = List.class)
    @GetMapping("/meshterms/tree")
    public List<MeshtermTreeView> findAllTrees(){
        List<MeshtermTree> meshtermTrees = treeRepository.findAll();
        List<MeshtermTreeView> meshtermTreeViews = new ArrayList<>();
        for (MeshtermTree meshtermTree : meshtermTrees) {
            meshtermTreeViews.add(new MeshtermTreeView(meshtermTree.getMeshtermTreePK().getMeshId(),
                    meshtermTree.getMeshtermTreePK().getTreeParentId(),
                    meshtermTree.getMeshtermTreePK().getTreeNodeId(),
                    meshtermTree.getMeshterm().getName(),
                    meshtermTreehasChild(meshtermTree)));
        }
        return meshtermTreeViews;
    }

    @ApiOperation(value = "View mesh term trees by mesh id")
    @GetMapping("/meshterms/tree/meshid/{id}")
    List<MeshtermTreeView> findAllTreesByMeshId(@PathVariable String id) {
        List<MeshtermTree> meshtermTrees = treeRepository.findByMeshId(id);
        List<MeshtermTreeView> meshtermTreeViews = new ArrayList<>();
        for (MeshtermTree meshtermTree : meshtermTrees) {
            meshtermTreeViews.add(new MeshtermTreeView(meshtermTree.getMeshtermTreePK().getMeshId(),
                    meshtermTree.getMeshtermTreePK().getTreeParentId(),
                    meshtermTree.getMeshtermTreePK().getTreeNodeId(),
                    meshtermTree.getMeshterm().getName(),
                    meshtermTreehasChild(meshtermTree)));
        }
        return meshtermTreeViews;
    }

    /*
     * When I click (category) Organisms [B], return mesh terms with BXX (return mesh terms where node=BXX)
     */
    @ApiOperation(value = "View mesh term trees by tree node id starting with category letter")
    @GetMapping("/meshterms/tree/nodeid/{nodeid}")
    List<MeshtermTreeView> findAllTreesByTreeNodeIdStartingWith(@PathVariable String nodeid) {
        List<MeshtermTree> meshtermTrees = treeRepository.findByTreeNodeIdStartingWithOrderByMeshtermTreePK(nodeid);
        List<MeshtermTreeView> meshtermTreeViews = new ArrayList<>();
        for (MeshtermTree meshtermTree : meshtermTrees) {
            meshtermTreeViews.add(new MeshtermTreeView(meshtermTree.getMeshtermTreePK().getMeshId(),
                    meshtermTree.getMeshtermTreePK().getTreeParentId(),
                    meshtermTree.getMeshtermTreePK().getTreeNodeId(),
                    meshtermTree.getMeshterm().getName(),
                    meshtermTreehasChild(meshtermTree)));
        }
        return meshtermTreeViews;
    }

     /*
     * When I click (meshterm) Eukaryota [B01], return mesh terms with B01.XXX (return mesh terms where parent=B01)
     * When I click (meshterm) Animals [B01.050], return mesh terms with B01.050.XXX (return mesh terms where parent=B01.050)
      */
    @ApiOperation(value = "View mesh term trees by tree parent id")
    @GetMapping("/meshterms/tree/parentid/{parentid}")
    List<MeshtermTreeView> findAllTreesByParentNodeId(@PathVariable String parentid) {
        List<MeshtermTree> meshtermTrees = treeRepository.findByTreeParentIdOrderByMeshtermTreePK(parentid);
        List<MeshtermTreeView> meshtermTreeViews = new ArrayList<>();
        for (MeshtermTree meshtermTree : meshtermTrees) {
            meshtermTreeViews.add(new MeshtermTreeView(meshtermTree.getMeshtermTreePK().getMeshId(),
                    meshtermTree.getMeshtermTreePK().getTreeParentId(),
                    meshtermTree.getMeshtermTreePK().getTreeNodeId(),
                    meshtermTree.getMeshterm().getName(),
                    meshtermTreehasChild(meshtermTree)));
        }
        return meshtermTreeViews;
    }

    boolean meshtermTreehasChild(MeshtermTree meshtermTree) {
        String meshtermTreeId;
        if (meshtermTree.getMeshtermTreePK().getTreeParentId().isEmpty()) {
            meshtermTreeId = meshtermTree.getMeshtermTreePK().getTreeNodeId();
        }
        else {
            meshtermTreeId = meshtermTree.getMeshtermTreePK().getTreeParentId() + "." +
                    meshtermTree.getMeshtermTreePK().getTreeNodeId();
        }
        MeshtermTree meshtermTreeObject = treeRepository.findOneByTreeParentId(meshtermTreeId);
        if (meshtermTreeObject==null) {
            return false;
        }
        return true;
    }

}
