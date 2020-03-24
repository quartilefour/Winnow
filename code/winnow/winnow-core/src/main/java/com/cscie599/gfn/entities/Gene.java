/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cscie599.gfn.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 *
 * @author PulkitBhanot
 */
@Entity
@Table(name = "gene")
@NamedQueries({
    @NamedQuery(name = "Gene.findAll", query = "SELECT g FROM Gene g"),
    @NamedQuery(name = "Gene.findByGeneId", query = "SELECT g FROM Gene g WHERE g.geneId = :geneId"),
    @NamedQuery(name = "Gene.findBySymbol", query = "SELECT g FROM Gene g WHERE g.symbol = :symbol"),
    @NamedQuery(name = "Gene.findByType", query = "SELECT g FROM Gene g WHERE g.type = :type"),
    @NamedQuery(name = "Gene.findByDescription", query = "SELECT g FROM Gene g WHERE g.description = :description"),
    @NamedQuery(name = "Gene.findByPublicationCount", query = "SELECT g FROM Gene g WHERE g.publicationCount = :publicationCount"),
    @NamedQuery(name = "Gene.findBySynonym", query = "SELECT g FROM Gene g WHERE g.synonym = :synonym"),
    @NamedQuery(name = "Gene.findByModificationDate", query = "SELECT g FROM Gene g WHERE g.modificationDate = :modificationDate"),
    @NamedQuery(name = "Gene.findByCountModificationTime", query = "SELECT g FROM Gene g WHERE g.countModificationTime = :countModificationTime")})
public class Gene implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "gene_id", nullable = false, length = 20)
    private String geneId;
    @Column(name = "symbol", length = 40)
    private String symbol;
    @Column(name = "type", length = 20)
    private String type;
    @Column(name = "description", length = 2147483647)
    private String description;
    @Column(name = "publication_count")
    private Integer publicationCount;
    @Column(name = "synonym", length = 20)
    private String synonym;
    @Column(name = "modification_date")
    @Temporal(TemporalType.DATE)
    private Date modificationDate;
    @Column(name = "count_modification_time")
    @Temporal(TemporalType.TIMESTAMP)
    private Date countModificationTime;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gene")
    private Collection<GenePublication> genePublicationCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gene")
    private Collection<GeneGene> geneGeneCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gene1")
    private Collection<GeneGene> geneGeneCollection1;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gene")
    private Collection<GeneMeshterm> geneMeshtermCollection;
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "gene")
    private Collection<GeneGoterm> geneGotermCollection;

    public Gene() {
    }

    public Gene(String geneId) {
        this.geneId = geneId;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getSymbol() {
        return symbol;
    }

    public void setSymbol(String symbol) {
        this.symbol = symbol;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Integer getPublicationCount() {
        return publicationCount;
    }

    public void setPublicationCount(Integer publicationCount) {
        this.publicationCount = publicationCount;
    }

    public String getSynonym() {
        return synonym;
    }

    public void setSynonym(String synonym) {
        this.synonym = synonym;
    }

    public Date getModificationDate() {
        return modificationDate;
    }

    public void setModificationDate(Date modificationDate) {
        this.modificationDate = modificationDate;
    }

    public Date getCountModificationTime() {
        return countModificationTime;
    }

    public void setCountModificationTime(Date countModificationTime) {
        this.countModificationTime = countModificationTime;
    }

    public Collection<GenePublication> getGenePublicationCollection() {
        return genePublicationCollection;
    }

    public void setGenePublicationCollection(Collection<GenePublication> genePublicationCollection) {
        this.genePublicationCollection = genePublicationCollection;
    }

    public Collection<GeneGene> getGeneGeneCollection() {
        return geneGeneCollection;
    }

    public void setGeneGeneCollection(Collection<GeneGene> geneGeneCollection) {
        this.geneGeneCollection = geneGeneCollection;
    }

    public Collection<GeneGene> getGeneGeneCollection1() {
        return geneGeneCollection1;
    }

    public void setGeneGeneCollection1(Collection<GeneGene> geneGeneCollection1) {
        this.geneGeneCollection1 = geneGeneCollection1;
    }

    public Collection<GeneMeshterm> getGeneMeshtermCollection() {
        return geneMeshtermCollection;
    }

    public void setGeneMeshtermCollection(Collection<GeneMeshterm> geneMeshtermCollection) {
        this.geneMeshtermCollection = geneMeshtermCollection;
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
        hash += (geneId != null ? geneId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof Gene)) {
            return false;
        }
        Gene other = (Gene) object;
        if ((this.geneId == null && other.geneId != null) || (this.geneId != null && !this.geneId.equals(other.geneId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.Gene[ geneId=" + geneId + " ]";
    }
    
}
