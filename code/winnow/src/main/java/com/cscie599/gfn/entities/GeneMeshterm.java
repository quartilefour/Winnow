/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import javax.persistence.Column;
import javax.persistence.EmbeddedId;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 *
 * @author bhanotp
 */
@Entity
@Table(name = "gene_meshterm")
@NamedQueries({
    @NamedQuery(name = "GeneMeshterm.findAll", query = "SELECT g FROM GeneMeshterm g"),
    @NamedQuery(name = "GeneMeshterm.findByGeneId", query = "SELECT g FROM GeneMeshterm g WHERE g.geneMeshtermPK.geneId = :geneId"),
    @NamedQuery(name = "GeneMeshterm.findByMeshId", query = "SELECT g FROM GeneMeshterm g WHERE g.geneMeshtermPK.meshId = :meshId"),
    @NamedQuery(name = "GeneMeshterm.findByPValue", query = "SELECT g FROM GeneMeshterm g WHERE g.pValue = :pValue"),
    @NamedQuery(name = "GeneMeshterm.findByPublicationCount", query = "SELECT g FROM GeneMeshterm g WHERE g.publicationCount = :publicationCount")})
public class GeneMeshterm implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GeneMeshtermPK geneMeshtermPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "p_value", precision = 17, scale = 17)
    private Double pValue;
    @Column(name = "publication_count")
    private Integer publicationCount;
    @JoinColumn(name = "gene_id", referencedColumnName = "gene_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Gene gene;
    @JoinColumn(name = "mesh_id", referencedColumnName = "mesh_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Meshterm meshterm;

    public GeneMeshterm() {
    }

    public GeneMeshterm(GeneMeshtermPK geneMeshtermPK) {
        this.geneMeshtermPK = geneMeshtermPK;
    }

    public GeneMeshterm(String geneId, String meshId) {
        this.geneMeshtermPK = new GeneMeshtermPK(geneId, meshId);
    }

    public GeneMeshtermPK getGeneMeshtermPK() {
        return geneMeshtermPK;
    }

    public void setGeneMeshtermPK(GeneMeshtermPK geneMeshtermPK) {
        this.geneMeshtermPK = geneMeshtermPK;
    }

    public Double getPValue() {
        return pValue;
    }

    public void setPValue(Double pValue) {
        this.pValue = pValue;
    }

    public Integer getPublicationCount() {
        return publicationCount;
    }

    public void setPublicationCount(Integer publicationCount) {
        this.publicationCount = publicationCount;
    }

    public Gene getGene() {
        return gene;
    }

    public void setGene(Gene gene) {
        this.gene = gene;
    }

    public Meshterm getMeshterm() {
        return meshterm;
    }

    public void setMeshterm(Meshterm meshterm) {
        this.meshterm = meshterm;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (geneMeshtermPK != null ? geneMeshtermPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneMeshterm)) {
            return false;
        }
        GeneMeshterm other = (GeneMeshterm) object;
        if ((this.geneMeshtermPK == null && other.geneMeshtermPK != null) || (this.geneMeshtermPK != null && !this.geneMeshtermPK.equals(other.geneMeshtermPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GeneMeshterm[ geneMeshtermPK=" + geneMeshtermPK + " ]";
    }
    
}
