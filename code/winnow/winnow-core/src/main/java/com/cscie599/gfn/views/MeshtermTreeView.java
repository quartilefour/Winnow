package com.cscie599.gfn.views;

import java.util.*;

public class MeshtermTreeView implements Comparable{
    private String meshId;
    private String treeParentId;
    private String treeNodeId;
    private String meshName;
    private String fullNodeId;
    private boolean hasChild;
    private Set<MeshtermTreeView> childNodes;
    public MeshtermTreeView(String meshId, String treeParentId, String treeNodeId, String meshName, boolean hasChild) {
        this.meshId = meshId.trim();
        this.treeParentId = treeParentId.trim();
        this.treeNodeId = treeNodeId.trim();
        this.meshName = meshName.trim();
        this.hasChild = hasChild;
        this.fullNodeId = this.treeParentId +"." + this.treeNodeId;
        childNodes = new TreeSet<>(new Comparator<MeshtermTreeView>() {
            @Override
            public int compare(MeshtermTreeView o1, MeshtermTreeView o2) {
                return o1.getTreeNodeId().compareTo(o2.getTreeNodeId());
            }
        });
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

    public void setHasChild(boolean hasChild) {
        this.hasChild = hasChild;
    }

    public Set<MeshtermTreeView> getChildNodes() {
        return childNodes;
    }

    public void setChildNodes(Set<MeshtermTreeView> childNodes) {
        this.childNodes = childNodes;
    }

    public String getFullNodeId() {
        return fullNodeId;
    }

    public void setFullNodeId(String fullNodeId) {
        this.fullNodeId = fullNodeId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeshtermTreeView that = (MeshtermTreeView) o;
        return fullNodeId.equals(that.fullNodeId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(fullNodeId);
    }

    @Override
    public String toString() {
        return "MeshtermTreeView{" +
                "meshId='" + meshId + '\'' +
                ", treeParentId='" + treeParentId + '\'' +
                ", treeNodeId='" + treeNodeId + '\'' +
                ", meshName='" + meshName + '\'' +
                ", fullNodeId='" + fullNodeId + '\'' +
                ", hasChild=" + hasChild +
                ", childNodes=" + childNodes +
                '}';
    }

    @Override
    public int compareTo(Object o) {
        MeshtermTreeView otherObject = (MeshtermTreeView) o;
        return this.meshId.compareTo(otherObject.getMeshId());
    }
}
