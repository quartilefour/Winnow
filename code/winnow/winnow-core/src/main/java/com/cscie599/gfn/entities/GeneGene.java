/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author PulkitBhanot
 */
@Entity
@Table(name = "gene_gene")
@NamedQueries({
    @NamedQuery(name = "GeneGene.findAll", query = "SELECT g FROM GeneGene g"),
    @NamedQuery(name = "GeneGene.findByGeneId", query = "SELECT g FROM GeneGene g WHERE g.geneGenePK.geneId = :geneId"),
    @NamedQuery(name = "GeneGene.findByOtherGeneId", query = "SELECT g FROM GeneGene g WHERE g.geneGenePK.otherGeneId = :otherGeneId"),
    @NamedQuery(name = "GeneGene.findByRelationshipId", query = "SELECT g FROM GeneGene g WHERE g.geneGenePK.relationshipId = :relationshipId")})
public class GeneGene implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GeneGenePK geneGenePK;
    @JoinColumn(name = "gene_id", referencedColumnName = "gene_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Gene gene;
    @JoinColumn(name = "other_gene_id", referencedColumnName = "gene_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Gene gene1;
    @JoinColumn(name = "relationship_id", referencedColumnName = "relationship_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private GeneRelationship geneRelationship;

    public GeneGene() {
    }

    public GeneGene(GeneGenePK geneGenePK) {
        this.geneGenePK = geneGenePK;
    }

    public GeneGene(int taxId, String geneId, String otherGeneId, int otherTaxId, String relationshipId) {
        this.geneGenePK = new GeneGenePK(taxId, geneId, otherGeneId, otherTaxId, relationshipId);
    }

    public GeneGenePK getGeneGenePK() {
        return geneGenePK;
    }

    public void setGeneGenePK(GeneGenePK geneGenePK) {
        this.geneGenePK = geneGenePK;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public Gene getGene1() {
        return gene1;
    }

    public void setGene1(Gene gene1) {
        this.gene1 = gene1;
    }

    public GeneRelationship getGeneRelationship() {
        return geneRelationship;
    }

    public void setGeneRelationship(GeneRelationship geneRelationship) {
        this.geneRelationship = geneRelationship;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (geneGenePK != null ? geneGenePK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneGene)) {
            return false;
        }
        GeneGene other = (GeneGene) object;
        if ((this.geneGenePK == null && other.geneGenePK != null) || (this.geneGenePK != null && !this.geneGenePK.equals(other.geneGenePK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.GeneGene[ geneGenePK=" + geneGenePK + " ]";
    }

}
