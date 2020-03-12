/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import java.util.Collection;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;

/**
 *
 * @author bhanotp
 */
@Entity
@Table(name = "author")
@NamedQueries({
    @NamedQuery(name = "Author.findAll", query = "SELECT a FROM Author a"),
    @NamedQuery(name = "Author.findByAuthorId", query = "SELECT a FROM Author a WHERE a.authorId = :authorId"),
    @NamedQuery(name = "Author.findByForeName", query = "SELECT a FROM Author a WHERE a.foreName = :foreName"),
    @NamedQuery(name = "Author.findByLastName", query = "SELECT a FROM Author a WHERE a.lastName = :lastName")})
public class Author implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "author_id", nullable = false, length = 50)
    private String authorId;
    @Column(name = "fore_name", length = 30)
    private String foreName;
    @Column(name = "last_name", length = 30)
    private String lastName;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "author")
    private Collection<PublicationAuthor> publicationAuthorCollection;

    public Author() {
    }

    public Author(String authorId) {
        this.authorId = authorId;
    }

    public String getAuthorId() {
        return authorId;
    }

    public void setAuthorId(String authorId) {
        this.authorId = authorId;
    }

    public String getForeName() {
        return foreName;
    }

    public void setForeName(String foreName) {
        this.foreName = foreName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Collection<PublicationAuthor> getPublicationAuthorCollection() {
        return publicationAuthorCollection;
    }

    public void setPublicationAuthorCollection(Collection<PublicationAuthor> publicationAuthorCollection) {
        this.publicationAuthorCollection = publicationAuthorCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (authorId != null ? authorId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Author)) {
            return false;
        }
        Author other = (Author) object;
        if ((this.authorId == null && other.authorId != null) || (this.authorId != null && !this.authorId.equals(other.authorId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.Author[ authorId=" + authorId + " ]";
    }
    
}
