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
public class GeneMeshtermPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "gene_id", nullable = false, length = 20)
    private String geneId;
    @Basic(optional = false)
    @Column(name = "mesh_id", nullable = false, length = 20)
    private String meshId;

    public GeneMeshtermPK() {
    }

    public GeneMeshtermPK(String geneId, String meshId) {
        this.geneId = geneId;
        this.meshId = meshId;
    }

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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (geneId != null ? geneId.hashCode() : 0);
        hash += (meshId != null ? meshId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneMeshtermPK)) {
            return false;
        }
        GeneMeshtermPK other = (GeneMeshtermPK) object;
        if ((this.geneId == null && other.geneId != null) || (this.geneId != null && !this.geneId.equals(other.geneId))) {
            return false;
        }
        if ((this.meshId == null && other.meshId != null) || (this.meshId != null && !this.meshId.equals(other.meshId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GeneMeshtermPK[ geneId=" + geneId + ", meshId=" + meshId + " ]";
    }
    
}
