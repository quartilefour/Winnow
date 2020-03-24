/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Collection;
import java.util.Date;

/**
 *
 * @author PulkitBhanot
 */
@Entity
@Table(name = "search")
@NamedQueries({
    @NamedQuery(name = "Search.findAll", query = "SELECT s FROM Search s"),
    @NamedQuery(name = "Search.findBySearchId", query = "SELECT s FROM Search s WHERE s.searchId = :searchId"),
    @NamedQuery(name = "Search.findByCreatedDate", query = "SELECT s FROM Search s WHERE s.createdDate = :createdDate"),
    @NamedQuery(name = "Search.findBySearchName", query = "SELECT s FROM Search s WHERE s.searchName = :searchName"),
    @NamedQuery(name = "Search.findByDeletedDate", query = "SELECT s FROM Search s WHERE s.deletedDate = :deletedDate"),
    @NamedQuery(name = "Search.findByUpdatedAt", query = "SELECT s FROM Search s WHERE s.updatedAt = :updatedAt"),
    @NamedQuery(name = "Search.findByQueryType", query = "SELECT s FROM Search s WHERE s.queryType = :queryType")})
public class Search implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "search_id", nullable = false, length = 20)
    private String searchId;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "search_name", length = 20)
    private String searchName;
    @Lob
    @Column(name = "search_query")
    private Object searchQuery;
    @Column(name = "deleted_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @Column(name = "query_type", length = 20)
    private String queryType;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "search")
    private Collection<UserSearchSharing> userSearchSharingCollection;
    @JoinColumn(name = "team_id", referencedColumnName = "team_id")
    @ManyToOne
    private Team teamId;
    @JoinColumn(name = "created_by", referencedColumnName = "user_id")
    @ManyToOne
    private User createdBy;

    public Search() {
    }

    public Search(String searchId) {
        this.searchId = searchId;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public String getSearchName() {
        return searchName;
    }

    public void setSearchName(String searchName) {
        this.searchName = searchName;
    }

    public Object getSearchQuery() {
        return searchQuery;
    }

    public void setSearchQuery(Object searchQuery) {
        this.searchQuery = searchQuery;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getQueryType() {
        return queryType;
    }

    public void setQueryType(String queryType) {
        this.queryType = queryType;
    }

    public Collection<UserSearchSharing> getUserSearchSharingCollection() {
        return userSearchSharingCollection;
    }

    public void setUserSearchSharingCollection(Collection<UserSearchSharing> userSearchSharingCollection) {
        this.userSearchSharingCollection = userSearchSharingCollection;
    }

    public Team getTeamId() {
        return teamId;
    }

    public void setTeamId(Team teamId) {
        this.teamId = teamId;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (searchId != null ? searchId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Search)) {
            return false;
        }
        Search other = (Search) object;
        if ((this.searchId == null && other.searchId != null) || (this.searchId != null && !this.searchId.equals(other.searchId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.Search[ searchId=" + searchId + " ]";
    }
    
}
