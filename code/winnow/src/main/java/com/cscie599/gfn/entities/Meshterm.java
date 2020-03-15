/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author bhanotp
 */
@Entity
@Table(name = "meshterm")
@NamedQueries({
    @NamedQuery(name = "Meshterm.findAll", query = "SELECT m FROM Meshterm m"),
    @NamedQuery(name = "Meshterm.findByMeshId", query = "SELECT m FROM Meshterm m WHERE m.meshId = :meshId"),
    @NamedQuery(name = "Meshterm.findByParentDescriptorId", query = "SELECT m FROM Meshterm m WHERE m.parentDescriptorId = :parentDescriptorId"),
    @NamedQuery(name = "Meshterm.findByPublicationCount", query = "SELECT m FROM Meshterm m WHERE m.publicationCount = :publicationCount"),
    @NamedQuery(name = "Meshterm.findByDateCreated", query = "SELECT m FROM Meshterm m WHERE m.dateCreated = :dateCreated"),
    @NamedQuery(name = "Meshterm.findByDateRevised", query = "SELECT m FROM Meshterm m WHERE m.dateRevised = :dateRevised"),
    @NamedQuery(name = "Meshterm.findByNote", query = "SELECT m FROM Meshterm m WHERE m.note = :note"),
    @NamedQuery(name = "Meshterm.findBySupplementalId", query = "SELECT m FROM Meshterm m WHERE m.supplementalId = :supplementalId"),
    @NamedQuery(name = "Meshterm.findByName", query = "SELECT m FROM Meshterm m WHERE m.name = :name")})
public class Meshterm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "mesh_id", nullable = false, length = 20)
    private String meshId;
    @Column(name = "parent_descriptor_id", length = 20)
    private String parentDescriptorId;
    @Column(name = "publication_count")
    private Integer publicationCount;
    @Column(name = "date_created")
    @Temporal(TemporalType.DATE)
    private Date dateCreated;
    @Column(name = "date_revised")
    @Temporal(TemporalType.DATE)
    private Date dateRevised;
    @Column(name = "note", length = 100)
    private String note;
    @Column(name = "supplemental_id", length = 20)
    private String supplementalId;
    @Column(name = "name", length = 30)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meshterm")
    private Collection<PublicationMeshterm> publicationMeshtermCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "meshterm")
    private Collection<GeneMeshterm> geneMeshtermCollection;

    public Meshterm() {
    }

    public Meshterm(String meshId) {
        this.meshId = meshId;
    }

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    public String getParentDescriptorId() {
        return parentDescriptorId;
    }

    public void setParentDescriptorId(String parentDescriptorId) {
        this.parentDescriptorId = parentDescriptorId;
    }

    public Integer getPublicationCount() {
        return publicationCount;
    }

    public void setPublicationCount(Integer publicationCount) {
        this.publicationCount = publicationCount;
    }

    public Date getDateCreated() {
        return dateCreated;
    }

    public void setDateCreated(Date dateCreated) {
        this.dateCreated = dateCreated;
    }

    public Date getDateRevised() {
        return dateRevised;
    }

    public void setDateRevised(Date dateRevised) {
        this.dateRevised = dateRevised;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getSupplementalId() {
        return supplementalId;
    }

    public void setSupplementalId(String supplementalId) {
        this.supplementalId = supplementalId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<PublicationMeshterm> getPublicationMeshtermCollection() {
        return publicationMeshtermCollection;
    }

    public void setPublicationMeshtermCollection(Collection<PublicationMeshterm> publicationMeshtermCollection) {
        this.publicationMeshtermCollection = publicationMeshtermCollection;
    }

    public Collection<GeneMeshterm> getGeneMeshtermCollection() {
        return geneMeshtermCollection;
    }

    public void setGeneMeshtermCollection(Collection<GeneMeshterm> geneMeshtermCollection) {
        this.geneMeshtermCollection = geneMeshtermCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (meshId != null ? meshId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Meshterm)) {
            return false;
        }
        Meshterm other = (Meshterm) object;
        if ((this.meshId == null && other.meshId != null) || (this.meshId != null && !this.meshId.equals(other.meshId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.Meshterm[ meshId=" + meshId + " ]";
    }
    
}
