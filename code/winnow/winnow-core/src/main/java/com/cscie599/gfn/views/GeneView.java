package com.cscie599.gfn.views;

public class GeneView {
    private String geneId;
    private String description;
    private String symbol;

    public GeneView(String geneId, String description, String symbol) {
        this.geneId = geneId.trim();
        this.description = description.trim();
        this.symbol = symbol.trim();
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
