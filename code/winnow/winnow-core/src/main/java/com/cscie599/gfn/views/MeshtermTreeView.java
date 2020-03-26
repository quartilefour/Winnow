package com.cscie599.gfn.views;

public class MeshtermTreeView {
    private String meshId;
    private String treeParentId;
    private String treeNodeId;
    private String meshName;
    private boolean hasChild;

    public MeshtermTreeView(String meshId, String treeParentId, String treeNodeId, String meshName, boolean hasChild) {
        this.meshId = meshId;
        this.treeParentId = treeParentId;
        this.treeNodeId = treeNodeId;
        this.meshName = meshName;
        this.hasChild = hasChild;
    }

    public String getMeshId() {
        return meshId;
    }

    public String getTreeParentId() {
        return treeParentId;
    }

    public String getTreeNodeId() {
        return treeNodeId;
    }

    public String getMeshName() {
        return meshName;
    }

    public boolean getHasChild() { return hasChild; }
}
