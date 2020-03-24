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
 * @author esantora
 */
@Entity
@Table(name = "user_role")
@NamedQueries({
    @NamedQuery(name = "UserRole.findAll", query = "SELECT u FROM UserRole u"),
    @NamedQuery(name = "UserRole.findByRoleId", query = "SELECT u FROM UserRole u WHERE u.userRolePK.roleId = :roleId"),
    @NamedQuery(name = "UserRole.findByUserId", query = "SELECT u FROM UserRole u WHERE u.userRolePK.userId = :userId"),
    @NamedQuery(name = "UserRole.findByCreatedDate", query = "SELECT u FROM UserRole u WHERE u.createdDate = :createdDate"),
    @NamedQuery(name = "UserRole.findByDeletedDate", query = "SELECT u FROM UserRole u WHERE u.deletedDate = :deletedDate")})
public class UserRole implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserRolePK userRolePK;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "deleted_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;
    @JoinColumn(name = "role_id", referencedColumnName = "role_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Role role;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public UserRole() {
    }

    public UserRole(UserRolePK userRolePK) {
        this.userRolePK = userRolePK;
    }

    public UserRole(String roleId, int userId) {
        this.userRolePK = new UserRolePK(roleId, userId);
    }

    public UserRolePK getUserRolePK() {
        return userRolePK;
    }

    public void setUserRolePK(UserRolePK userRolePK) {
        this.userRolePK = userRolePK;
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

    public Role getRole() {
        return role;
    }

    public void setRole(Role role) {
        this.role = role;
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
        hash += (userRolePK != null ? userRolePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserRole)) {
            return false;
        }
        UserRole other = (UserRole) object;
        return (this.userRolePK != null || other.userRolePK == null) && (this.userRolePK == null || this.userRolePK.equals(other.userRolePK));
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.UserRole[ userRolePK=" + userRolePK + " ]";
    }
    
}
