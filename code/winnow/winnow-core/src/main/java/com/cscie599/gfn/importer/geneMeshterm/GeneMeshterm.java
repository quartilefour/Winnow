package com.cscie599.gfn.importer.geneMeshterm;

public class GeneMeshterm {
    String geneId;
    String meshId;
    String pValue;
    String publicationCount;

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    public String getpValue() {
        return pValue;
    }

    public void setpValue(String pValue) {
        this.pValue = pValue;
    }

    public String getPublicationCount() {
        return publicationCount;
    }

    public void setPublicationCount(String publicationCount) {
        this.publicationCount = publicationCount;
    }

    @Override
    public String toString() {
        return "GeneMeshterm{" +
                "geneId='" + geneId + '\'' +
                ", meshId='" + meshId + '\'' +
                ", pValue='" + pValue + '\'' +
                ", publicationCount='" + publicationCount + '\'' +
                '}';
    }
}
