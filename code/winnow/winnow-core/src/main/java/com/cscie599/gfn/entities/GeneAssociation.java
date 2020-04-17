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
 * @author Lila Mullany
 */
@Entity
@Table(name = "gene_association")
@NamedQueries({
        @NamedQuery(name = "GeneAssociation.findAll", query = "SELECT g FROM GeneAssociation g"),
        @NamedQuery(name = "GeneAssociation.findByGeneId", query = "SELECT g FROM GeneAssociation g WHERE g.geneAssociationPK.geneId = :geneId"),
        @NamedQuery(name = "GeneAssociation.findByOtherGeneId", query = "SELECT g FROM GeneAssociation g WHERE g.geneAssociationPK.otherGeneId = :otherGeneId"),
        @NamedQuery(name = "GeneAssociation.findByPValue", query = "SELECT g FROM GeneAssociation g WHERE g.pValue = :pValue"),
        @NamedQuery(name = "GeneAssociation.findByPublicationCount", query = "SELECT g FROM GeneAssociation g WHERE g.publicationCount = :publicationCount")})
public class GeneAssociation implements Serializable {

    private static final long serialVersionUID = 1L;
    @EmbeddedId
    protected GeneAssociationPK geneAssociationPK;
    // @Max(value=?)  @Min(value=?)//if you know range of your decimal fields consider using these annotations to enforce field validation
    @Column(name = "p_value", precision = 17, scale = 17)
    private Double pValue;
    @Column(name = "publication_count")
    private Integer publicationCount;
    @JoinColumn(name = "gene_id", referencedColumnName = "gene_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Gene gene;
    @JoinColumn(name = "other_gene_id", referencedColumnName = "gene_id", nullable = false, insertable = false, updatable = false)
    @ManyToOne(optional = false)
    private Gene otherGene;

    public GeneAssociation() {
    }

    public GeneAssociation(GeneAssociationPK geneAssociationPK) {
        this.geneAssociationPK = geneAssociationPK;
    }

    public GeneAssociation(String geneId, String otherGeneId) {
        this.geneAssociationPK = new GeneAssociationPK(geneId, otherGeneId);
    }

    public GeneAssociationPK getGeneAssociationPK() {
        return geneAssociationPK;
    }

    public void setGeneAssociationPK(GeneAssociationPK geneAssociationPK) {
        this.geneAssociationPK = geneAssociationPK;
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

    public Gene getOtherGene() {
        return otherGene;
    }

    public void setOtherGene(Gene otherGene) {
        this.otherGene = otherGene;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (geneAssociationPK != null ? geneAssociationPK.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof GeneAssociation)) {
            return false;
        }
        GeneAssociation other = (GeneAssociation) object;
        if ((this.geneAssociationPK == null && other.geneAssociationPK != null) || (this.geneAssociationPK != null && !this.geneAssociationPK.equals(other.geneAssociationPK))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.GeneAssociation[ geneAssociationPK=" + geneAssociationPK + " ]";
    }
}
