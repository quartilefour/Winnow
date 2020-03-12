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
public class UserTeamPK implements Serializable {

    @Basic(optional = false)
    @Column(name = "team_id", nullable = false, length = 20)
    private String teamId;
    @Basic(optional = false)
    @Column(name = "user_id", nullable = false)
    private int userId;

    public UserTeamPK() {
    }

    public UserTeamPK(String teamId, int userId) {
        this.teamId = teamId;
        this.userId = userId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
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
        hash += (teamId != null ? teamId.hashCode() : 0);
        hash += (int) userId;
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof UserTeamPK)) {
            return false;
        }
        UserTeamPK other = (UserTeamPK) object;
        if ((this.teamId == null && other.teamId != null) || (this.teamId != null && !this.teamId.equals(other.teamId))) {
            return false;
        }
        if (this.userId != other.userId) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.UserTeamPK[ teamId=" + teamId + ", userId=" + userId + " ]";
    }
    
}
