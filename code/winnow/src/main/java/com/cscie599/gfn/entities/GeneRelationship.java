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
@Table(name = "gene_relationship")
@NamedQueries({
    @NamedQuery(name = "GeneRelationship.findAll", query = "SELECT g FROM GeneRelationship g"),
    @NamedQuery(name = "GeneRelationship.findByRelationshipId", query = "SELECT g FROM GeneRelationship g WHERE g.relationshipId = :relationshipId"),
    @NamedQuery(name = "GeneRelationship.findByName", query = "SELECT g FROM GeneRelationship g WHERE g.name = :name")})
public class GeneRelationship implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "relationship_id", nullable = false, length = 20)
    private String relationshipId;
    @Column(name = "name", length = 50)
    private String name;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "geneRelationship")
    private Collection<GeneGene> geneGeneCollection;

    public GeneRelationship() {
    }

    public GeneRelationship(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getRelationshipId() {
        return relationshipId;
    }

    public void setRelationshipId(String relationshipId) {
        this.relationshipId = relationshipId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Collection<GeneGene> getGeneGeneCollection() {
        return geneGeneCollection;
    }

    public void setGeneGeneCollection(Collection<GeneGene> geneGeneCollection) {
        this.geneGeneCollection = geneGeneCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (relationshipId != null ? relationshipId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneRelationship)) {
            return false;
        }
        GeneRelationship other = (GeneRelationship) object;
        if ((this.relationshipId == null && other.relationshipId != null) || (this.relationshipId != null && !this.relationshipId.equals(other.relationshipId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GeneRelationship[ relationshipId=" + relationshipId + " ]";
    }
    
}
