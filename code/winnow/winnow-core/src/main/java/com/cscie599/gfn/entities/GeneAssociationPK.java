package com.cscie599.gfn.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author Lila Mullany
 */
@Embeddable
public class GeneAssociationPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "gene_id", nullable = false, length = 20)
    private String geneId;
    @Basic(optional = false)
    @Column(name = "other_gene_id", nullable = false, length = 20)
    private String otherGeneId;

    public GeneAssociationPK() {
    }

    public GeneAssociationPK(String geneId, String otherGeneId) {
        this.geneId = geneId;
        this.otherGeneId = otherGeneId;
    }

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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (geneId != null ? geneId.hashCode() : 0);
        hash += (otherGeneId != null ? otherGeneId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneAssociationPK)) {
            return false;
        }
        GeneAssociationPK other = (GeneAssociationPK) object;
        if ((this.geneId == null && other.geneId != null) || (this.geneId != null && !this.geneId.equals(other.geneId))) {
            return false;
        }
        if ((this.otherGeneId == null && other.otherGeneId != null) || (this.otherGeneId != null && !this.otherGeneId.equals(other.otherGeneId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GeneMeshtermPK[ geneId=" + geneId + ", meshId=" + otherGeneId + " ]";
    }
}
