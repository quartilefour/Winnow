package com.cscie599.gfn.importer.genegroup;

/**
 *
 * @author PulkitBhanot
 */
public class GeneGroup {
    String geneId;
    int taxId;
    String relationship;
    String otherGeneId;
    int otherTaxId;

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

    public int getTaxId() {
        return taxId;
    }

    public void setTaxId(int taxId) {
        this.taxId = taxId;
    }

    public int getOtherTaxId() {
        return otherTaxId;
    }

    public void setOtherTaxId(int otherTaxId) {
        this.otherTaxId = otherTaxId;
    }

    @Override
    public String toString() {
        return "GeneGroup{" +
                "geneId='" + geneId + '\'' +
                ", taxId=" + taxId +
                ", relationship='" + relationship + '\'' +
                ", otherGeneId='" + otherGeneId + '\'' +
                ", otherTaxId=" + otherTaxId +
                '}';
    }
}
