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
import java.util.Set;

/**
 *
 * @author bhanotp
 */
@Entity
@Table(name = "user")
@NamedQueries({
    @NamedQuery(name = "User.findAll", query = "SELECT u FROM User u"),
    @NamedQuery(name = "User.findByUserId", query = "SELECT u FROM User u WHERE u.userId = :userId"),
    @NamedQuery(name = "User.findByUserEmail", query = "SELECT u FROM User u WHERE u.userEmail = :userEmail"),
    @NamedQuery(name = "User.findByFirstName", query = "SELECT u FROM User u WHERE u.firstName = :firstName"),
    @NamedQuery(name = "User.findByLastName", query = "SELECT u FROM User u WHERE u.lastName = :lastName"),
    @NamedQuery(name = "User.findByCreatedAt", query = "SELECT u FROM User u WHERE u.createdAt = :createdAt"),
    @NamedQuery(name = "User.findByUpdatedAt", query = "SELECT u FROM User u WHERE u.updatedAt = :updatedAt")})
public class User implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "user_id", nullable = false)
    private Integer userId;
    @Column(name = "user_email", length = 254)
    private String userEmail;
    @Column(name = "user_password", length = 512)
    private String userPassword;
    @Column(name = "first_name", length = 40)
    private String firstName;
    @Column(name = "last_name", length = 40)
    private String lastName;
    @Column(name = "created_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;
    @Column(name = "updated_at")
    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
    @OneToMany(mappedBy = "sharedBy")
    private Collection<UserSearchSharing> userSearchSharingCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<UserSearchSharing> userSearchSharingCollection1;
    @OneToOne(cascade = CascadeType.ALL, mappedBy = "user")
    private Address address;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Collection<UserTeam> userTeamCollection;
    @OneToMany(mappedBy = "createdBy")
    private Collection<Search> searchCollection;
    @ManyToMany
    private Set<Role> roles;


    public User() {
    }

    public User(Integer userId) {
        this.userId = userId;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public String getUserEmail() {
        return userEmail;
    }

    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public Date getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(Date updatedAt) {
        this.updatedAt = updatedAt;
    }

    public Collection<UserSearchSharing> getUserSearchSharingCollection() {
        return userSearchSharingCollection;
    }

    public void setUserSearchSharingCollection(Collection<UserSearchSharing> userSearchSharingCollection) {
        this.userSearchSharingCollection = userSearchSharingCollection;
    }

    public Collection<UserSearchSharing> getUserSearchSharingCollection1() {
        return userSearchSharingCollection1;
    }

    public void setUserSearchSharingCollection1(Collection<UserSearchSharing> userSearchSharingCollection1) {
        this.userSearchSharingCollection1 = userSearchSharingCollection1;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public Collection<UserTeam> getUserTeamCollection() {
        return userTeamCollection;
    }

    public void setUserTeamCollection(Collection<UserTeam> userTeamCollection) {
        this.userTeamCollection = userTeamCollection;
    }

    public Collection<Search> getSearchCollection() {
        return searchCollection;
    }

    public void setSearchCollection(Collection<Search> searchCollection) {
        this.searchCollection = searchCollection;
    }

    public Set<Role> getRoles() {
        return roles;
    }

    public void setRoles(Set<Role> roles) {
        this.roles = roles;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (userId != null ? userId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof User)) {
            return false;
        }
        User other = (User) object;
        if ((this.userId == null && other.userId != null) || (this.userId != null && !this.userId.equals(other.userId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.User[ userId=" + userId + " ]";
    }
    
}
