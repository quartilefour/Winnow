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
 * @author lilamullany
 */
@Embeddable
public class MeshtermTreePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "mesh_id", nullable = false, length = 20)
    private String meshId;
    @Basic(optional = false)
    @Column(name = "tree_id", nullable = false, length = 100)
    private String treeId;

    public MeshtermTreePK() {
    }

    public MeshtermTreePK(String meshId, String treeId) {
        this.meshId = meshId;
        this.treeId = treeId;
    }

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    public String getTreeId() {
        return treeId;
    }

    public void setTreeId(String treeId) {
        this.treeId = treeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (meshId != null ? meshId.hashCode() : 0);
        hash += (treeId != null ? treeId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MeshtermTreePK)) {
            return false;
        }
        MeshtermTreePK other = (MeshtermTreePK) object;
        if ((this.meshId == null && other.meshId != null) || (this.meshId != null && !this.meshId.equals(other.meshId))) {
            return false;
        }
        if ((this.treeId == null && other.treeId != null) || (this.treeId != null && !this.treeId.equals(other.treeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.MeshtermTreePK[ meshId=" + meshId + ", treeId=" + treeId + " ]";
    }
    
}
