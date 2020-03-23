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
@Table(name = "user_team")
@NamedQueries({
    @NamedQuery(name = "UserTeam.findAll", query = "SELECT u FROM UserTeam u"),
    @NamedQuery(name = "UserTeam.findByTeamId", query = "SELECT u FROM UserTeam u WHERE u.userTeamPK.teamId = :teamId"),
    @NamedQuery(name = "UserTeam.findByUserId", query = "SELECT u FROM UserTeam u WHERE u.userTeamPK.userId = :userId"),
    @NamedQuery(name = "UserTeam.findByCreatedDate", query = "SELECT u FROM UserTeam u WHERE u.createdDate = :createdDate"),
    @NamedQuery(name = "UserTeam.findByDeletedDate", query = "SELECT u FROM UserTeam u WHERE u.deletedDate = :deletedDate")})
public class UserTeam implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected UserTeamPK userTeamPK;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "deleted_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;
    @JoinColumn(name = "team_id", referencedColumnName = "team_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Team team;
    @JoinColumn(name = "user_id", referencedColumnName = "user_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private User user;

    public UserTeam() {
    }

    public UserTeam(UserTeamPK userTeamPK) {
        this.userTeamPK = userTeamPK;
    }

    public UserTeam(String teamId, int userId) {
        this.userTeamPK = new UserTeamPK(teamId, userId);
    }

    public UserTeamPK getUserTeamPK() {
        return userTeamPK;
    }

    public void setUserTeamPK(UserTeamPK userTeamPK) {
        this.userTeamPK = userTeamPK;
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

    public Team getTeam() {
        return team;
    }

    public void setTeam(Team team) {
        this.team = team;
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
        hash += (userTeamPK != null ? userTeamPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserTeam)) {
            return false;
        }
        UserTeam other = (UserTeam) object;
        if ((this.userTeamPK == null && other.userTeamPK != null) || (this.userTeamPK != null && !this.userTeamPK.equals(other.userTeamPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.UserTeam[ userTeamPK=" + userTeamPK + " ]";
    }
    
}
