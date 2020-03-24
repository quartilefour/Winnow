package com.cscie599.gfn.views;

public class GeneView {
    private String geneId;
    private String description;
    private String symbol;

    public GeneView(String geneId, String description, String symbol) {
        this.geneId = geneId;
        this.description = description;
        this.symbol = symbol;
    }

    public String getGeneId() {
        return geneId;
    }

    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }
}
