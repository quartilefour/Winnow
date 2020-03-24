package com.cscie599.gfn.views;

public class MeshtermTreeView {
    private String meshId;
    private String treeParentId;
    private String treeNodeId;
    private String meshName;

    public MeshtermTreeView(String meshId, String treeParentId, String treeNodeId, String meshName) {
        this.meshId = meshId;
        this.treeParentId = treeParentId;
        this.treeNodeId = treeNodeId;
        this.meshName = meshName;
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
}
