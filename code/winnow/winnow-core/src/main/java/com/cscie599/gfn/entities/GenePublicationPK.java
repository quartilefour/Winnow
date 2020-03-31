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
 * @author PulkitBhanot
 */
@Embeddable
public class GenePublicationPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "gene_id", nullable = false, length = 20)
    private String geneId;
    @Basic(optional = false)
    @Column(name = "publication_id", nullable = false, length = 20)
    private String publicationId;
    @Basic(optional = false)
    @Column(name = "tax_id", nullable = false)
    private int taxId;

    public GenePublicationPK() {
    }

    public GenePublicationPK(String geneId, String publicationId, int taxId) {
        this.geneId = geneId;
        this.publicationId = publicationId;
        this.taxId = taxId;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public int getTaxId() {
        return taxId;
    }

    public void setTaxId(int taxId) {
        this.taxId = taxId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (geneId != null ? geneId.hashCode() : 0);
        hash += (publicationId != null ? publicationId.hashCode() : 0);
        hash += (int) taxId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GenePublicationPK)) {
            return false;
        }
        GenePublicationPK other = (GenePublicationPK) object;
        if ((this.geneId == null && other.geneId != null) || (this.geneId != null && !this.geneId.equals(other.geneId))) {
            return false;
        }
        if ((this.publicationId == null && other.publicationId != null) || (this.publicationId != null && !this.publicationId.equals(other.publicationId))) {
            return false;
        }
        if (this.taxId != other.taxId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GenePublicationPK[ geneId=" + geneId + ", publicationId=" + publicationId + ", taxId=" + taxId + " ]";
    }
    
}
