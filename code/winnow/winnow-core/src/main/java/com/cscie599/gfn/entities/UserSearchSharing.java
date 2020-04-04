/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

/**
 *
 * @author PulkitBhanot
 */
@Entity
@Table(name = "user_search_sharing")
@NamedQueries({
    @NamedQuery(name = "UserSearchSharing.findAll", query = "SELECT u FROM UserSearchSharing u"),
    @NamedQuery(name = "UserSearchSharing.findBySearchId", query = "SELECT u FROM UserSearchSharing u WHERE u.userSearchSharingPK.searchId = :searchId"),
    @NamedQuery(name = "UserSearchSharing.findByUserId", query = "SELECT u FROM UserSearchSharing u WHERE u.userSearchSharingPK.userId = :userId"),
    @NamedQuery(name = "UserSearchSharing.findBySharedDate", query = "SELECT u FROM UserSearchSharing u WHERE u.sharedDate = :sharedDate"),
    @NamedQuery(name = "UserSearchSharing.findByDeletedDate", query = "SELECT u FROM UserSearchSharing u WHERE u.deletedDate = :deletedDate")})
public class UserSearchSharing implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserSearchSharingPK userSearchSharingPK;
    @Column(name = "shared_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date sharedDate;
    @Column(name = "deleted_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;
    @JoinColumn(name = "search_id", referencedColumnName = "search_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Search search;
    @JoinColumn(name = "shared_by", referencedColumnName = "user_id")
    @ManyToOne
    private User sharedBy;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public UserSearchSharing() {
    }

    public UserSearchSharing(UserSearchSharingPK userSearchSharingPK) {
        this.userSearchSharingPK = userSearchSharingPK;
    }

    public UserSearchSharing(long searchId, int userId) {
        this.userSearchSharingPK = new UserSearchSharingPK(searchId, userId);
    }

    public UserSearchSharingPK getUserSearchSharingPK() {
        return userSearchSharingPK;
    }

    public void setUserSearchSharingPK(UserSearchSharingPK userSearchSharingPK) {
        this.userSearchSharingPK = userSearchSharingPK;
    }

    public Date getSharedDate() {
        return sharedDate;
    }

    public void setSharedDate(Date sharedDate) {
        this.sharedDate = sharedDate;
    }

    public Date getDeletedDate() {
        return deletedDate;
    }

    public void setDeletedDate(Date deletedDate) {
        this.deletedDate = deletedDate;
    }

    public Search getSearch() {
        return search;
    }

    public void setSearch(Search search) {
        this.search = search;
    }

    public User getSharedBy() {
        return sharedBy;
    }

    public void setSharedBy(User sharedBy) {
        this.sharedBy = sharedBy;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userSearchSharingPK != null ? userSearchSharingPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserSearchSharing)) {
            return false;
        }
        UserSearchSharing other = (UserSearchSharing) object;
        if ((this.userSearchSharingPK == null && other.userSearchSharingPK != null) || (this.userSearchSharingPK != null && !this.userSearchSharingPK.equals(other.userSearchSharingPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.UserSearchSharing[ userSearchSharingPK=" + userSearchSharingPK + " ]";
    }
    
}
