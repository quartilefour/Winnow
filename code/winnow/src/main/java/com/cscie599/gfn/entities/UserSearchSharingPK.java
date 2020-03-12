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
public class UserSearchSharingPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "search_id", nullable = false, length = 20)
    private String searchId;
    @Basic(optional = false)
    @Column(name = "user_id", nullable = false)
    private int userId;

    public UserSearchSharingPK() {
    }

    public UserSearchSharingPK(String searchId, int userId) {
        this.searchId = searchId;
        this.userId = userId;
    }

    public String getSearchId() {
        return searchId;
    }

    public void setSearchId(String searchId) {
        this.searchId = searchId;
    }

    public int getUserId() {
        return userId;
    }

    public void setUserId(int userId) {
        this.userId = userId;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (searchId != null ? searchId.hashCode() : 0);
        hash += (int) userId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserSearchSharingPK)) {
            return false;
        }
        UserSearchSharingPK other = (UserSearchSharingPK) object;
        if ((this.searchId == null && other.searchId != null) || (this.searchId != null && !this.searchId.equals(other.searchId))) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.UserSearchSharingPK[ searchId=" + searchId + ", userId=" + userId + " ]";
    }
    
}
