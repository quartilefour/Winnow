package com.cscie599.gfn.importer.genegroup;

/**
 *
 * @author PulkitBhanot
 */
public class GeneGroup {
    String geneId;
    String relationship;
    String otherGeneId;

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getRelationship() {
        return relationship;
    }

    public void setRelationship(String relationship) {
        this.relationship = relationship;
    }

    public String getOtherGeneId() {
        return otherGeneId;
    }

    public void setOtherGeneId(String otherGeneId) {
        this.otherGeneId = otherGeneId;
    }

    @Override
    public String toString() {
        return "GeneGroup{" +
                "geneId='" + geneId + '\'' +
                ", relationship='" + relationship + '\'' +
                ", otherGeneId='" + otherGeneId + '\'' +
                '}';
    }
}
