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
@Table(name = "team")
@NamedQueries({
    @NamedQuery(name = "Team.findAll", query = "SELECT t FROM Team t"),
    @NamedQuery(name = "Team.findByTeamId", query = "SELECT t FROM Team t WHERE t.teamId = :teamId"),
    @NamedQuery(name = "Team.findByTeamLeadId", query = "SELECT t FROM Team t WHERE t.teamLeadId = :teamLeadId"),
    @NamedQuery(name = "Team.findByDescription", query = "SELECT t FROM Team t WHERE t.description = :description")})
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "team_id", nullable = false, length = 20)
    private String teamId;
    @Column(name = "team_lead_id", length = 20)
    private String teamLeadId;
    @Column(name = "description", length = 100)
    private String description;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "team")
    private Collection<UserTeam> userTeamCollection;
    @OneToMany(mappedBy = "teamId")
    private Collection<Search> searchCollection;

    public Team() {
    }

    public Team(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamId() {
        return teamId;
    }

    public void setTeamId(String teamId) {
        this.teamId = teamId;
    }

    public String getTeamLeadId() {
        return teamLeadId;
    }

    public void setTeamLeadId(String teamLeadId) {
        this.teamLeadId = teamLeadId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (teamId != null ? teamId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Team)) {
            return false;
        }
        Team other = (Team) object;
        if ((this.teamId == null && other.teamId != null) || (this.teamId != null && !this.teamId.equals(other.teamId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.Team[ teamId=" + teamId + " ]";
    }
    
}
