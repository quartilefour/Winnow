/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Embeddable;
import java.io.Serializable;

/**
 *
 * @author esantora
 */
@Embeddable
public class UserRolePK implements Serializable {

    @Basic(optional = false)
    @Column(name = "role_id", nullable = false, length = 20)
    private String roleId;
    @Basic(optional = false)
    @Column(name = "user_id", nullable = false)
    private int userId;

    public UserRolePK() {
    }

    public UserRolePK(String roleId, int userId) {
        this.roleId = roleId;
        this.userId = userId;
    }

    public String getRoleId() {
        return roleId;
    }

    public void setRoleId(String roleId) {
        this.roleId = roleId;
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
        hash += (roleId != null ? roleId.hashCode() : 0);
        hash += (int) userId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserRolePK)) {
            return false;
        }
        UserRolePK other = (UserRolePK) object;
        if ((this.roleId == null && other.roleId != null) || (this.roleId != null && !this.roleId.equals(other.roleId))) {
            return false;
        }
        return this.userId == other.userId;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.UserRolePK[ roleId=" + roleId + ", userId=" + userId + " ]";
    }
    
}
