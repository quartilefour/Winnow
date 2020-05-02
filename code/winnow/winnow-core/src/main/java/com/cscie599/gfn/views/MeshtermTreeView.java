package com.cscie599.gfn.views;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.*;

public class MeshtermTreeView implements Comparable{
    @JsonProperty("id")
    private String meshId;
    @JsonProperty("p")
    private String treeParentId;
    @JsonProperty("t")
    private String treeNodeId;
    @JsonProperty("name")
    private String meshName;
    @JsonProperty("meshIndex")
    private String fullNodeId;
    @JsonProperty("hasChild")
    private boolean hasChild;
    @JsonProperty("children")
    private Set<MeshtermTreeView> childNodes;
    public MeshtermTreeView(String meshId, String treeParentId, String treeNodeId, String meshName, boolean hasChild) {
        this.meshId = meshId.trim();
        this.treeParentId = treeParentId.trim();
        this.treeNodeId = treeNodeId.trim();
        this.hasChild = hasChild;
        this.fullNodeId = this.treeParentId + "." + this.treeNodeId;
        this.meshName = meshName.trim() + " [" + this.fullNodeId + "]";
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
