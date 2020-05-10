package com.cscie599.gfn.views;

public class GeneMeshtermView {
    private long index;
    private String geneId;
    private String description;
    private String symbol;
    private String meshId;
    private String name;
    private int publicationCount;
    private double pValue;

    public GeneMeshtermView(long index, String geneId, String description, String symbol, String meshId, String name, int publicationCount, double pValue) {
        this.index = index;
        this.geneId = geneId;
        this.description = description;
        this.symbol = symbol;
        this.meshId = meshId;
        this.name = name;
        this.publicationCount = publicationCount;
        this.pValue = pValue;
    }

    public long getIndex() { return index; }

    public String getGeneId() {
        return geneId;
    }

    public String getDescription() {
        return description;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getMeshId() {
        return meshId;
    }

    public String getName() {
        return name;
    }

    public Integer getPublicationCount() {
        return publicationCount;
    }

    public Double getPValue() {
        return pValue;
    }
}
