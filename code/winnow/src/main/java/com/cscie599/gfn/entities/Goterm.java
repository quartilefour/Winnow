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
@Table(name = "goterm")
@NamedQueries({
    @NamedQuery(name = "Goterm.findAll", query = "SELECT g FROM Goterm g"),
    @NamedQuery(name = "Goterm.findByGoId", query = "SELECT g FROM Goterm g WHERE g.goId = :goId"),
    @NamedQuery(name = "Goterm.findByDefinition", query = "SELECT g FROM Goterm g WHERE g.definition = :definition"),
    @NamedQuery(name = "Goterm.findByXrefs", query = "SELECT g FROM Goterm g WHERE g.xrefs = :xrefs"),
    @NamedQuery(name = "Goterm.findByLabel", query = "SELECT g FROM Goterm g WHERE g.label = :label")})
public class Goterm implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "go_id", nullable = false, length = 20)
    private String goId;
    @Column(name = "definition", length = 2147483647)
    private String definition;
    @Column(name = "xrefs", length = 200)
    private String xrefs;
    @Column(name = "label", length = 200)
    private String label;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "goterm")
    private Collection<GeneGoterm> geneGotermCollection;

    public Goterm() {
    }

    public Goterm(String goId) {
        this.goId = goId;
    }

    public String getGoId() {
        return goId;
    }

    public void setGoId(String goId) {
        this.goId = goId;
    }

    public String getDefinition() {
        return definition;
    }

    public void setDefinition(String definition) {
        this.definition = definition;
    }

    public String getXrefs() {
        return xrefs;
    }

    public void setXrefs(String xrefs) {
        this.xrefs = xrefs;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public Collection<GeneGoterm> getGeneGotermCollection() {
        return geneGotermCollection;
    }

    public void setGeneGotermCollection(Collection<GeneGoterm> geneGotermCollection) {
        this.geneGotermCollection = geneGotermCollection;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (goId != null ? goId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Goterm)) {
            return false;
        }
        Goterm other = (Goterm) object;
        if ((this.goId == null && other.goId != null) || (this.goId != null && !this.goId.equals(other.goId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.Goterm[ goId=" + goId + " ]";
    }
    
}
