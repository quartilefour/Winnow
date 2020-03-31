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
    @Column(name = "tree_parent_id", nullable = false, length = 120)
    private String treeParentId;
    @Basic(optional = false)
    @Column(name = "tree_node_id", nullable = false, length = 6)
    private String treeNodeId;

    public MeshtermTreePK() {
    }

    public MeshtermTreePK(String meshId, String treeParentId, String treeNodeId) {
        this.meshId = meshId;
        this.treeParentId = treeParentId;
        this.treeNodeId = treeNodeId;
    }

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    public String getTreeParentId() {
        return treeParentId;
    }

    public void setTreeParentId(String treeParentId) {
        this.treeParentId = treeParentId;
    }

    public String getTreeNodeId() {
        return treeNodeId;
    }

    public void setTreeNodeId(String treeNodeId) {
        this.treeNodeId = treeNodeId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (meshId != null ? meshId.hashCode() : 0);
        hash += (treeParentId != null ? treeParentId.hashCode() : 0);
        hash += (treeNodeId != null ? treeNodeId.hashCode() : 0);
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
        if ((this.treeParentId == null && other.treeParentId != null) || (this.treeParentId != null && !this.treeParentId.equals(other.treeParentId))) {
            return false;
        }
        if ((this.treeNodeId == null && other.treeNodeId != null) || (this.treeNodeId != null && !this.treeNodeId.equals(other.treeNodeId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.MeshtermTreePK[ meshId=" + meshId + ", treeParentId=" + treeParentId + ", treeNodeId=" + treeNodeId + " ]";
    }
    
}
