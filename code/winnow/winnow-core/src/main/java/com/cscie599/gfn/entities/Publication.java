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
 * @author PulkitBhanot
 */
@Entity
@Table(name = "publication")
@NamedQueries({
    @NamedQuery(name = "Publication.findAll", query = "SELECT p FROM Publication p"),
    @NamedQuery(name = "Publication.findByPublicationId", query = "SELECT p FROM Publication p WHERE p.publicationId = :publicationId"),
    @NamedQuery(name = "Publication.findByCompletedDate", query = "SELECT p FROM Publication p WHERE p.completedDate = :completedDate"),
    @NamedQuery(name = "Publication.findByDateRevised", query = "SELECT p FROM Publication p WHERE p.dateRevised = :dateRevised"),
    @NamedQuery(name = "Publication.findByTitle", query = "SELECT p FROM Publication p WHERE p.title = :title"),
    @NamedQuery(name = "Publication.findByLanguage", query = "SELECT p FROM Publication p WHERE p.language = :language")})
public class Publication implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "publication_id", nullable = false, length = 20)
    private String publicationId;
    @Column(name = "completed_date")
    @Temporal(TemporalType.DATE)
    private Date completedDate;
    @Column(name = "date_revised")
    @Temporal(TemporalType.DATE)
    private Date dateRevised;
    @Column(name = "title", length = 2147483647)
    private String title;
    @Column(name = "language", length = 20)
    private String language;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publication")
    private Collection<PublicationAuthor> publicationAuthorCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publication")
    private Collection<GenePublication> genePublicationCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "publication")
    private Collection<PublicationMeshterm> publicationMeshtermCollection;

    public Publication() {
    }

    public Publication(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public Date getCompletedDate() {
        return completedDate;
    }

    public void setCompletedDate(Date completedDate) {
        this.completedDate = completedDate;
    }

    public Date getDateRevised() {
        return dateRevised;
    }

    public void setDateRevised(Date dateRevised) {
        this.dateRevised = dateRevised;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public Collection<PublicationAuthor> getPublicationAuthorCollection() {
        return publicationAuthorCollection;
    }

    public void setPublicationAuthorCollection(Collection<PublicationAuthor> publicationAuthorCollection) {
        this.publicationAuthorCollection = publicationAuthorCollection;
    }

    public Collection<GenePublication> getGenePublicationCollection() {
        return genePublicationCollection;
    }

    public void setGenePublicationCollection(Collection<GenePublication> genePublicationCollection) {
        this.genePublicationCollection = genePublicationCollection;
    }

    public Collection<PublicationMeshterm> getPublicationMeshtermCollection() {
        return publicationMeshtermCollection;
    }

    public void setPublicationMeshtermCollection(Collection<PublicationMeshterm> publicationMeshtermCollection) {
        this.publicationMeshtermCollection = publicationMeshtermCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (publicationId != null ? publicationId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Publication)) {
            return false;
        }
        Publication other = (Publication) object;
        if ((this.publicationId == null && other.publicationId != null) || (this.publicationId != null && !this.publicationId.equals(other.publicationId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.Publication[ publicationId=" + publicationId + " ]";
    }
    
}
