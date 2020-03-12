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
public class PublicationAuthorPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "publication_id", nullable = false, length = 20)
    private String publicationId;
    @Basic(optional = false)
    @Column(name = "author_id", nullable = false, length = 50)
    private String authorId;

    public PublicationAuthorPK() {
    }

    public PublicationAuthorPK(String publicationId, String authorId) {
        this.publicationId = publicationId;
        this.authorId = authorId;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (publicationId != null ? publicationId.hashCode() : 0);
        hash += (authorId != null ? authorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof PublicationAuthorPK)) {
            return false;
        }
        PublicationAuthorPK other = (PublicationAuthorPK) object;
        if ((this.publicationId == null && other.publicationId != null) || (this.publicationId != null && !this.publicationId.equals(other.publicationId))) {
            return false;
        }
        if ((this.authorId == null && other.authorId != null) || (this.authorId != null && !this.authorId.equals(other.authorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.PublicationAuthorPK[ publicationId=" + publicationId + ", authorId=" + authorId + " ]";
    }
    
}
