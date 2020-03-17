/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author lilamullany
 */
@Entity
@Table(name = "meshterm_tree")
@NamedQueries({
    @NamedQuery(name = "MeshtermTree.findAll", query = "SELECT m FROM MeshtermTree m"),
    @NamedQuery(name = "MeshtermTree.findByMeshId", query = "SELECT m FROM MeshtermTree m WHERE m.meshtermTreePK.meshId = :meshId"),
    @NamedQuery(name = "MeshtermTree.findByTreeId", query = "SELECT m FROM MeshtermTree m WHERE m.meshtermTreePK.treeId = :treeId")})
public class MeshtermTree implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected MeshtermTreePK meshtermTreePK;
    @JoinColumn(name = "mesh_id", referencedColumnName = "mesh_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Meshterm meshterm;

    public MeshtermTree() {
    }

    public MeshtermTree(MeshtermTreePK meshtermTreePK) {
        this.meshtermTreePK = meshtermTreePK;
    }

    public MeshtermTree(String meshId, String treeId) {
        this.meshtermTreePK = new MeshtermTreePK(meshId, treeId);
    }

    public MeshtermTreePK getMeshtermTreePK() {
        return meshtermTreePK;
    }

    public void setMeshtermTreePK(MeshtermTreePK meshtermTreePK) {
        this.meshtermTreePK = meshtermTreePK;
    }

    public Meshterm getMeshterm() {
        return meshterm;
    }

    public void setMeshterm(Meshterm meshterm) {
        this.meshterm = meshterm;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (meshtermTreePK != null ? meshtermTreePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MeshtermTree)) {
            return false;
        }
        MeshtermTree other = (MeshtermTree) object;
        if ((this.meshtermTreePK == null && other.meshtermTreePK != null) || (this.meshtermTreePK != null && !this.meshtermTreePK.equals(other.meshtermTreePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.MeshtermTree[ meshtermTreePK=" + meshtermTreePK + " ]";
    }
    
}
