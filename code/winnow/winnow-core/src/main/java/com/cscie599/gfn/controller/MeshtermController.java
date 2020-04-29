package com.cscie599.gfn.controller;


import com.cscie599.gfn.entities.MeshtermCategory;
import com.cscie599.gfn.entities.MeshtermTree;
import com.cscie599.gfn.repository.MeshtermCategoryRepository;
import com.cscie599.gfn.repository.MeshtermTreeRepository;
import com.cscie599.gfn.views.MeshtermCategoryView;
import com.cscie599.gfn.views.MeshtermTreeView;
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
@Api(value = "MeSH terms", description = "Operations pertaining to MeSH terms in Gene Function Navigation")
public class MeshtermController {

    @Autowired
    MeshtermCategoryRepository meshtermCategoryRepository;

    @Autowired
    MeshtermTreeRepository meshtermTreeRepository;

    @ApiOperation(value = "View all MeSH term categories.", response = List.class)
    @GetMapping("/meshterms/category")
    public List<MeshtermCategoryView> findAllCategories() {
        List<MeshtermCategory> meshtermCategories = meshtermCategoryRepository.findAll();
        List<MeshtermCategoryView> meshtermCategoryViews = new ArrayList<>();
        for (MeshtermCategory meshtermCategory : meshtermCategories) {
            meshtermCategoryViews.add(new MeshtermCategoryView(meshtermCategory.getCategoryId(), meshtermCategory.getName()));
        }
        return meshtermCategoryViews;
    }

    @ApiOperation(value = "View all MeSH term tree records by tree node id starting with particular string.")
    @GetMapping("/meshterms/tree/nodeid/{nodeid}")
    public List<MeshtermTreeView> findAllTreesByTreeNodeIdStartingWith(@PathVariable String nodeid) {
        List<MeshtermTree> meshtermTrees = meshtermTreeRepository.findByTreeNodeIdStartingWithOrderByMeshtermTreePK(nodeid);
        return getMeshtermTreeViews(meshtermTrees);
    }

    @ApiOperation(value = "View all MeSH term tree records that have particular parent id.")
    @GetMapping("/meshterms/tree/parentid/{parentid}")
    public List<MeshtermTreeView> findAllTreesByParentNodeId(@PathVariable String parentid) {
        List<MeshtermTree> meshtermTrees = meshtermTreeRepository.findByTreeParentIdOrderByMeshtermTreePK(parentid);
        return getMeshtermTreeViews(meshtermTrees);
    }

    public List<MeshtermTreeView> getMeshtermTreeViews(List<MeshtermTree> meshtermTrees) {
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

    public boolean meshtermTreehasChild(MeshtermTree meshtermTree) {
        String meshtermTreeId;
        if (meshtermTree.getMeshtermTreePK().getTreeParentId().isEmpty()) {
            meshtermTreeId = meshtermTree.getMeshtermTreePK().getTreeNodeId();
        } else {
            meshtermTreeId = meshtermTree.getMeshtermTreePK().getTreeParentId() + "." +
                    meshtermTree.getMeshtermTreePK().getTreeNodeId();
        }
        MeshtermTree meshtermTreeObject = meshtermTreeRepository.findOneByTreeParentId(meshtermTreeId);
        return meshtermTreeObject != null;
    }

}
