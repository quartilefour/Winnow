/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author bhanotp
 */
@Entity
@Table(name = "publication_meshterm")
@NamedQueries({
    @NamedQuery(name = "PublicationMeshterm.findAll", query = "SELECT p FROM PublicationMeshterm p"),
    @NamedQuery(name = "PublicationMeshterm.findByPublicationId", query = "SELECT p FROM PublicationMeshterm p WHERE p.publicationMeshtermPK.publicationId = :publicationId"),
    @NamedQuery(name = "PublicationMeshterm.findByMeshId", query = "SELECT p FROM PublicationMeshterm p WHERE p.publicationMeshtermPK.meshId = :meshId"),
    @NamedQuery(name = "PublicationMeshterm.findByCreatedDate", query = "SELECT p FROM PublicationMeshterm p WHERE p.createdDate = :createdDate"),
    @NamedQuery(name = "PublicationMeshterm.findByDeletedDate", query = "SELECT p FROM PublicationMeshterm p WHERE p.deletedDate = :deletedDate")})
public class PublicationMeshterm implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PublicationMeshtermPK publicationMeshtermPK;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "deleted_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;
    @JoinColumn(name = "mesh_id", referencedColumnName = "mesh_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Meshterm meshterm;
    @JoinColumn(name = "publication_id", referencedColumnName = "publication_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Publication publication;

    public PublicationMeshterm() {
    }

    public PublicationMeshterm(PublicationMeshtermPK publicationMeshtermPK) {
        this.publicationMeshtermPK = publicationMeshtermPK;
    }

    public PublicationMeshterm(String publicationId, String meshId) {
        this.publicationMeshtermPK = new PublicationMeshtermPK(publicationId, meshId);
    }

    public PublicationMeshtermPK getPublicationMeshtermPK() {
        return publicationMeshtermPK;
    }

    public void setPublicationMeshtermPK(PublicationMeshtermPK publicationMeshtermPK) {
        this.publicationMeshtermPK = publicationMeshtermPK;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Meshterm getMeshterm() {
        return meshterm;
    }

    public void setMeshterm(Meshterm meshterm) {
        this.meshterm = meshterm;
    }

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (publicationMeshtermPK != null ? publicationMeshtermPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PublicationMeshterm)) {
            return false;
        }
        PublicationMeshterm other = (PublicationMeshterm) object;
        if ((this.publicationMeshtermPK == null && other.publicationMeshtermPK != null) || (this.publicationMeshtermPK != null && !this.publicationMeshtermPK.equals(other.publicationMeshtermPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.PublicationMeshterm[ publicationMeshtermPK=" + publicationMeshtermPK + " ]";
    }
    
}
