/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import java.util.Date;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author PulkitBhanot
 */
@Entity
@Table(name = "gene_goterm")
@NamedQueries({
    @NamedQuery(name = "GeneGoterm.findAll", query = "SELECT g FROM GeneGoterm g"),
    @NamedQuery(name = "GeneGoterm.findByGeneId", query = "SELECT g FROM GeneGoterm g WHERE g.geneGotermPK.geneId = :geneId"),
    @NamedQuery(name = "GeneGoterm.findByGoId", query = "SELECT g FROM GeneGoterm g WHERE g.geneGotermPK.goId = :goId"),
    @NamedQuery(name = "GeneGoterm.findByTaxId", query = "SELECT g FROM GeneGoterm g WHERE g.geneGotermPK.taxId = :taxId"),
    @NamedQuery(name = "GeneGoterm.findByCreatedDate", query = "SELECT g FROM GeneGoterm g WHERE g.createdDate = :createdDate"),
    @NamedQuery(name = "GeneGoterm.findByDeletedDate", query = "SELECT g FROM GeneGoterm g WHERE g.deletedDate = :deletedDate")})
public class GeneGoterm implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GeneGotermPK geneGotermPK;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "deleted_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;
    @JoinColumn(name = "gene_id", referencedColumnName = "gene_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Gene gene;
    @JoinColumn(name = "go_id", referencedColumnName = "go_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Goterm goterm;

    public GeneGoterm() {
    }

    public GeneGoterm(GeneGotermPK geneGotermPK) {
        this.geneGotermPK = geneGotermPK;
    }

    public GeneGoterm(String geneId, String goId, int taxId) {
        this.geneGotermPK = new GeneGotermPK(geneId, goId, taxId);
    }

    public GeneGotermPK getGeneGotermPK() {
        return geneGotermPK;
    }

    public void setGeneGotermPK(GeneGotermPK geneGotermPK) {
        this.geneGotermPK = geneGotermPK;
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

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public Goterm getGoterm() {
        return goterm;
    }

    public void setGoterm(Goterm goterm) {
        this.goterm = goterm;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (geneGotermPK != null ? geneGotermPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneGoterm)) {
            return false;
        }
        GeneGoterm other = (GeneGoterm) object;
        if ((this.geneGotermPK == null && other.geneGotermPK != null) || (this.geneGotermPK != null && !this.geneGotermPK.equals(other.geneGotermPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GeneGoterm[ geneGotermPK=" + geneGotermPK + " ]";
    }
    
}
