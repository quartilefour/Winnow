package com.cscie599.gfn.controller;


import com.cscie599.gfn.entities.MeshtermCategory;
import com.cscie599.gfn.entities.MeshtermTree;
import com.cscie599.gfn.repository.MeshtermCategoryRepository;
import com.cscie599.gfn.repository.MeshtermTreeRepository;
import com.cscie599.gfn.views.MeshtermCategoryView;
import com.cscie599.gfn.views.MeshtermTreeView;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@RestController
@RequestMapping("/api")
public class MeshtermController {

    private static final Log logger = LogFactory.getLog(MeshtermController.class);

    @Autowired
    MeshtermCategoryRepository meshtermCategoryRepository;

    @Autowired
    MeshtermTreeRepository meshtermTreeRepository;

    AtomicReference<Collection<MeshtermTreeView>> meshTermTreeView = new AtomicReference<>();

    /**
     * Gets all MeSH term categories.
     *
     * @return List of all MeSH term category views
     */
    @GetMapping("/meshterms/category")
    public List<MeshtermCategoryView> findAllCategories() {
        List<MeshtermCategory> meshtermCategories = meshtermCategoryRepository.findAll();
        List<MeshtermCategoryView> meshtermCategoryViews = new ArrayList<>();
        for (MeshtermCategory meshtermCategory : meshtermCategories) {
            meshtermCategoryViews.add(new MeshtermCategoryView(meshtermCategory.getCategoryId(), meshtermCategory.getName()));
        }
        return meshtermCategoryViews;
    }

    /**
     * Gets tree records with tree node id starting with nodeid.
     *
     * @param nodeid Tree node ID
     * @return List of MeSH term tree views
     */
    @GetMapping("/meshterms/tree/nodeid/{nodeid}")
    public List<MeshtermTreeView> findAllTreesByTreeNodeIdStartingWith(@PathVariable String nodeid) {
        List<MeshtermTree> meshtermTrees = meshtermTreeRepository.findByTreeNodeIdStartingWithOrderByMeshtermTreePK(nodeid);
        return getMeshtermTreeViews(meshtermTrees);
    }

    /**
     * Gets tree records with parent node id parentid.
     *
     * @param parentid Parent node ID
     * @return List of MeSH term tree views
     */
    @GetMapping("/meshterms/tree/parentid/{parentid}")
    public List<MeshtermTreeView> findAllTreesByParentNodeId(@PathVariable String parentid) {
        List<MeshtermTree> meshtermTrees = meshtermTreeRepository.findByTreeParentIdOrderByMeshtermTreePK(parentid);
        return getMeshtermTreeViews(meshtermTrees);
    }

    /**
     * Gets the entire MeSH term tree hierarchy as a nested object.
     *
     * @return Collection of all MeSH term tree views
     */
    @GetMapping("/meshterms/tree")
    public Collection<MeshtermTreeView> findEntireTree() {
        if (meshTermTreeView.get() == null) {
            Object[][] rawQueryResult = meshtermTreeRepository.findAllSortedByParentId();
            List<MeshtermCategory> meshtermCategories = meshtermCategoryRepository.findAll();
            meshTermTreeView.set(generateMeshtTermTree(rawQueryResult, meshtermCategories));
        }
        return meshTermTreeView.get();
    }

    public void invalidate(){
        meshTermTreeView.set(null);
    }

    /**
     * Returns a collection of nested objects of {@href MeshtermTreeView} from the passed in query result and the category list.
     */
    private Collection<MeshtermTreeView> generateMeshtTermTree(Object[][] rawQueryResult, List<MeshtermCategory> meshtermCategories) {
        Map<String, MeshtermTreeView> allTermsView = new HashMap<>();
        Map<String, MeshtermTreeView> returnTermsView = new HashMap<>();
        // Create inmemory representation of all the meshterms
        for (Object[] singleTerm : rawQueryResult) {
            String currentNodeId = ((String) singleTerm[4]).trim();
            MeshtermTreeView meshtermTreeView = allTermsView.get(currentNodeId);
            if (meshtermTreeView == null) {
                meshtermTreeView = new MeshtermTreeView((String) singleTerm[0], (String) singleTerm[1], (String) singleTerm[2], (String) singleTerm[3], false);
                allTermsView.put(currentNodeId, meshtermTreeView);
            }
        }
        // Create parent child relationship between the nodes.
        for (Object[] singleTerm : rawQueryResult) {

            String currentNodeId = ((String) singleTerm[4]).trim();
            // This is the parent node
            if (currentNodeId.startsWith(".")) {
                String id = currentNodeId.replaceAll("\\.", "");
                MeshtermTreeView currentMeshtermTreeView = allTermsView.get(id);
                returnTermsView.put(id, currentMeshtermTreeView);
                continue;
            }
            MeshtermTreeView currentMeshtermTreeView = allTermsView.get(currentNodeId);
            String superParentId = getParent(currentNodeId);
            MeshtermTreeView superParentView = returnTermsView.get(superParentId);
            if (superParentView == null) {
                superParentView = createParentPath(currentMeshtermTreeView, returnTermsView, allTermsView);
            }
            if (superParentView != null) {
                insertIntoSuperParent(superParentView, currentMeshtermTreeView, allTermsView);
            }
        }
        // join the records with the category list at the top
        Set<MeshtermTreeView> meshtermTreeViewTreeSet = new TreeSet<>();
        for (MeshtermCategory meshtermCategory : meshtermCategories) {
            MeshtermTreeView meshtermCategoryTreeView = new MeshtermTreeView(meshtermCategory.getCategoryId(), "", meshtermCategory.getCategoryId(), meshtermCategory.getName(), true);
            meshtermCategoryTreeView.setFullNodeId(meshtermCategory.getCategoryId());
            returnTermsView.forEach((key, value) -> {
                if (value != null && key.contains(meshtermCategoryTreeView.getMeshId())) {
                    logger.info("Adding " + key + "to parent " + meshtermCategory.getCategoryId());
                    value.setFullNodeId(value.getTreeNodeId());
                    meshtermCategoryTreeView.getChildNodes().add(value);
                }
            });
            meshtermTreeViewTreeSet.add(meshtermCategoryTreeView);
        }
        return meshtermTreeViewTreeSet;
    }

    /**
     * Adds a child node {@param currentMeshtermTreeView} at the appropriate level in the chain from {@param superParentView},
     */
    private void insertIntoSuperParent(MeshtermTreeView superParentView, MeshtermTreeView currentMeshtermTreeView, Map<String, MeshtermTreeView> allTermsView) {
        String[] tokens = currentMeshtermTreeView.getFullNodeId().split("\\.");
        String pathString = tokens[0];
        MeshtermTreeView iterableParentView = superParentView;
        for (int i = 1; i < tokens.length; i++) {
            pathString = pathString + "." + tokens[i];
            MeshtermTreeView currentPathMeshtermTreeView = allTermsView.get(pathString);
            if (currentPathMeshtermTreeView == null) {
                logger.warn("Unable to lookup parent, will skip this entire path in insert" + pathString);
                return;
            }
            iterableParentView.getChildNodes().add(currentPathMeshtermTreeView);
            iterableParentView = currentPathMeshtermTreeView;
        }
    }

    /**
     * Creates an entire parent chain for a given node in  {@param returnTermsView} adding any missing nodes from {@param allTermsView}
     */
    private MeshtermTreeView createParentPath(MeshtermTreeView currentMeshtermTreeView, Map<String, MeshtermTreeView> returnTermsView, Map<String, MeshtermTreeView> allTermsView) {
        String id = currentMeshtermTreeView.getFullNodeId();
        String[] tokens = id.split("\\.");
        String pathString = tokens[0];
        MeshtermTreeView superParentView = returnTermsView.get(pathString);

        if (superParentView == null) {
            superParentView = allTermsView.get("." + pathString);
            returnTermsView.put(pathString, superParentView);
        }
        if (tokens.length == 2) {
            returnTermsView.put(pathString, superParentView);
            return superParentView;
        }

        MeshtermTreeView iterableParentView = superParentView;
        if (iterableParentView == null) {
            logger.warn("Parent found to be null for id " + id);
            return null;
        }
        for (int i = 1; i < tokens.length; i++) {
            pathString = pathString + "." + tokens[i];
            MeshtermTreeView currentPathMeshtermTreeView = allTermsView.get(pathString);
            if (currentPathMeshtermTreeView == null) {
                logger.warn("Unable to lookup parent, will skip this entire path in createparentpath" + pathString);
                return null;
            }
            iterableParentView.getChildNodes().add(currentPathMeshtermTreeView);
            iterableParentView = currentPathMeshtermTreeView;
        }
        return superParentView;

    }

    /**
     * Returns the parent ID for a given node.
     */
    private String getParent(String id) {
        return id.split("\\.")[0];
    }

    /**
     * Returns a list of MeSH term tree views.
     *
     * @param meshtermTrees List of MeSH term trees
     * @return List of MeSH term tree views
     */
    private List<MeshtermTreeView> getMeshtermTreeViews(List<MeshtermTree> meshtermTrees) {
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

    /**
     * Checks if the MeSH term tree has at least one child.
     *
     * @param meshtermTree MeSH term tree record
     * @return Boolean true if record has at least one child else false
     */
    private boolean meshtermTreehasChild(MeshtermTree meshtermTree) {
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
