package com.cscie599.gfn.views;

public class GeneView {
    private String geneId;
    private String description;

    public GeneView(String geneId, String description) {
        this.geneId = geneId;
        this.description = description;
    }

    public String getGeneId() {
        return geneId;
    }

    public String getDescription() {
        return description;
    }
}
