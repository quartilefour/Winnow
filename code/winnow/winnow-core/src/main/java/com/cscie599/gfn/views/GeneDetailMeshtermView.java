package com.cscie599.gfn.views;

public class GeneDetailMeshtermView {
    private String meshId;
    private String name;
    private int publicationCount;
    private double pValue;

    public GeneDetailMeshtermView(String meshId, String name, int publicationCount, double pValue) {
        this.meshId = meshId;
        this.name = name;
        this.publicationCount = publicationCount;
        this.pValue = pValue;
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
