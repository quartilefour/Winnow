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
public class GeneGotermPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "gene_id", nullable = false, length = 20)
    private String geneId;
    @Basic(optional = false)
    @Column(name = "go_id", nullable = false, length = 20)
    private String goId;
    @Basic(optional = false)
    @Column(name = "tax_id", nullable = false)
    private int taxId;

    public GeneGotermPK() {
    }

    public GeneGotermPK(String geneId, String goId, int taxId) {
        this.geneId = geneId;
        this.goId = goId;
        this.taxId = taxId;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getGoId() {
        return goId;
    }

    public void setGoId(String goId) {
        this.goId = goId;
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
        hash += (goId != null ? goId.hashCode() : 0);
        hash += (int) taxId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneGotermPK)) {
            return false;
        }
        GeneGotermPK other = (GeneGotermPK) object;
        if ((this.geneId == null && other.geneId != null) || (this.geneId != null && !this.geneId.equals(other.geneId))) {
            return false;
        }
        if ((this.goId == null && other.goId != null) || (this.goId != null && !this.goId.equals(other.goId))) {
            return false;
        }
        if (this.taxId != other.taxId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GeneGotermPK[ geneId=" + geneId + ", goId=" + goId + ", taxId=" + taxId + " ]";
    }
    
}
