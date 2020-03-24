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
@Table(name = "gene_publication")
@NamedQueries({
    @NamedQuery(name = "GenePublication.findAll", query = "SELECT g FROM GenePublication g"),
    @NamedQuery(name = "GenePublication.findByGeneId", query = "SELECT g FROM GenePublication g WHERE g.genePublicationPK.geneId = :geneId"),
    @NamedQuery(name = "GenePublication.findByPublicationId", query = "SELECT g FROM GenePublication g WHERE g.genePublicationPK.publicationId = :publicationId"),
    @NamedQuery(name = "GenePublication.findByCreatedDate", query = "SELECT g FROM GenePublication g WHERE g.createdDate = :createdDate"),
    @NamedQuery(name = "GenePublication.findByDeletedDate", query = "SELECT g FROM GenePublication g WHERE g.deletedDate = :deletedDate")})
public class GenePublication implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GenePublicationPK genePublicationPK;
    @Column(name = "created_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date createdDate;
    @Column(name = "deleted_date")
    @Temporal(TemporalType.TIMESTAMP)
    private Date deletedDate;
    @JoinColumn(name = "gene_id", referencedColumnName = "gene_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Gene gene;
    @JoinColumn(name = "publication_id", referencedColumnName = "publication_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Publication publication;

    public GenePublication() {
    }

    public GenePublication(GenePublicationPK genePublicationPK) {
        this.genePublicationPK = genePublicationPK;
    }

    public GenePublication(String geneId, String publicationId) {
        this.genePublicationPK = new GenePublicationPK(geneId, publicationId);
    }

    public GenePublicationPK getGenePublicationPK() {
        return genePublicationPK;
    }

    public void setGenePublicationPK(GenePublicationPK genePublicationPK) {
        this.genePublicationPK = genePublicationPK;
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

    public Publication getPublication() {
        return publication;
    }

    public void setPublication(Publication publication) {
        this.publication = publication;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (genePublicationPK != null ? genePublicationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GenePublication)) {
            return false;
        }
        GenePublication other = (GenePublication) object;
        if ((this.genePublicationPK == null && other.genePublicationPK != null) || (this.genePublicationPK != null && !this.genePublicationPK.equals(other.genePublicationPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GenePublication[ genePublicationPK=" + genePublicationPK + " ]";
    }
    
}
