package com.cscie599.gfn.views;

import java.math.BigInteger;

public class GeneDetailCoOccuringGeneView {
    private String geneId;
    private String description;
    private String symbol;
    private BigInteger publicationCount;

    public GeneDetailCoOccuringGeneView(String geneId, String description, String symbol, BigInteger publicationCount) {
        this.geneId = geneId;
        this.description = description;
        this.symbol = symbol;
        this.publicationCount = publicationCount;
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

    public BigInteger getPublicationCount() {
        return publicationCount;
    }
}
