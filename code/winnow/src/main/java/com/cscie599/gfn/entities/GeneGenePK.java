/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;

/**
 *
 * @author bhanotp
 */
@Embeddable
public class GeneGenePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "gene_id", nullable = false, length = 20)
    private String geneId;
    @Basic(optional = false)
    @Column(name = "other_gene_id", nullable = false, length = 20)
    private String otherGeneId;
    @Basic(optional = false)
    @Column(name = "relationship_id", nullable = false, length = 20)
    private String relationshipId;

    public GeneGenePK() {
    }

    public GeneGenePK(String geneId, String otherGeneId, String relationshipId) {
        this.geneId = geneId;
        this.otherGeneId = otherGeneId;
        this.relationshipId = relationshipId;
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

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (geneId != null ? geneId.hashCode() : 0);
        hash += (otherGeneId != null ? otherGeneId.hashCode() : 0);
        hash += (relationshipId != null ? relationshipId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneGenePK)) {
            return false;
        }
        GeneGenePK other = (GeneGenePK) object;
        if ((this.geneId == null && other.geneId != null) || (this.geneId != null && !this.geneId.equals(other.geneId))) {
            return false;
        }
        if ((this.otherGeneId == null && other.otherGeneId != null) || (this.otherGeneId != null && !this.otherGeneId.equals(other.otherGeneId))) {
            return false;
        }
        if ((this.relationshipId == null && other.relationshipId != null) || (this.relationshipId != null && !this.relationshipId.equals(other.relationshipId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GeneGenePK[ geneId=" + geneId + ", otherGeneId=" + otherGeneId + ", relationshipId=" + relationshipId + " ]";
    }
    
}
