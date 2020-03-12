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
@Table(name = "publication_author")
@NamedQueries({
    @NamedQuery(name = "PublicationAuthor.findAll", query = "SELECT p FROM PublicationAuthor p"),
    @NamedQuery(name = "PublicationAuthor.findByPublicationId", query = "SELECT p FROM PublicationAuthor p WHERE p.publicationAuthorPK.publicationId = :publicationId"),
    @NamedQuery(name = "PublicationAuthor.findByAuthorId", query = "SELECT p FROM PublicationAuthor p WHERE p.publicationAuthorPK.authorId = :authorId"),
    @NamedQuery(name = "PublicationAuthor.findByCreationDate", query = "SELECT p FROM PublicationAuthor p WHERE p.creationDate = :creationDate")})
public class PublicationAuthor implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected PublicationAuthorPK publicationAuthorPK;
    @Column(name = "creation_date")
    @Temporal(TemporalType.DATE)
    private Date creationDate;
    @JoinColumn(name = "author_id", referencedColumnName = "author_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Author author;
    @JoinColumn(name = "publication_id", referencedColumnName = "publication_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Publication publication;

    public PublicationAuthor() {
    }

    public PublicationAuthor(PublicationAuthorPK publicationAuthorPK) {
        this.publicationAuthorPK = publicationAuthorPK;
    }

    public PublicationAuthor(String publicationId, String authorId) {
        this.publicationAuthorPK = new PublicationAuthorPK(publicationId, authorId);
    }

    public PublicationAuthorPK getPublicationAuthorPK() {
        return publicationAuthorPK;
    }

    public void setPublicationAuthorPK(PublicationAuthorPK publicationAuthorPK) {
        this.publicationAuthorPK = publicationAuthorPK;
    }

    public Date getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Date creationDate) {
        this.creationDate = creationDate;
    }

    public Author getAuthor() {
        return author;
    }

    public void setAuthor(Author author) {
        this.author = author;
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
        hash += (publicationAuthorPK != null ? publicationAuthorPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PublicationAuthor)) {
            return false;
        }
        PublicationAuthor other = (PublicationAuthor) object;
        if ((this.publicationAuthorPK == null && other.publicationAuthorPK != null) || (this.publicationAuthorPK != null && !this.publicationAuthorPK.equals(other.publicationAuthorPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.PublicationAuthor[ publicationAuthorPK=" + publicationAuthorPK + " ]";
    }
    
}
