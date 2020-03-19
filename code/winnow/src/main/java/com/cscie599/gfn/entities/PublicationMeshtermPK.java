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
public class PublicationMeshtermPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "publication_id", nullable = false, length = 20)
    private String publicationId;
    @Basic(optional = false)
    @Column(name = "mesh_id", nullable = false, length = 20)
    private String meshId;

    public PublicationMeshtermPK() {
    }

    public PublicationMeshtermPK(String publicationId, String meshId) {
        this.publicationId = publicationId;
        this.meshId = meshId;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
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
        hash += (publicationId != null ? publicationId.hashCode() : 0);
        hash += (meshId != null ? meshId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PublicationMeshtermPK)) {
            return false;
        }
        PublicationMeshtermPK other = (PublicationMeshtermPK) object;
        if ((this.publicationId == null && other.publicationId != null) || (this.publicationId != null && !this.publicationId.equals(other.publicationId))) {
            return false;
        }
        if ((this.meshId == null && other.meshId != null) || (this.meshId != null && !this.meshId.equals(other.meshId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.PublicationMeshtermPK[ publicationId=" + publicationId + ", meshId=" + meshId + " ]";
    }
    
}
