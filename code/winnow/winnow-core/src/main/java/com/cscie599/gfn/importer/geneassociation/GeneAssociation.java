package com.cscie599.gfn.importer.geneassociation;

public class GeneAssociation {
    String geneId;
    String otherGeneId;
    String pValue;
    String publicationCount;

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getOtherGeneId() {
        return otherGeneId;
    }

    public void setOtherGeneId(String otherGeneId) {
        this.otherGeneId = otherGeneId;
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
        return "GeneAssociation{" +
                "geneId='" + geneId + '\'' +
                ", otherGeneId='" + otherGeneId + '\'' +
                ", pValue='" + pValue + '\'' +
                ", publicationCount='" + publicationCount + '\'' +
                '}';
    }
}

