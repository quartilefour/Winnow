package com.cscie599.gfn.entities;

import javax.persistence.*;
import java.io.Serializable;

@Entity
@Table(name = "meshterm_category")
@NamedQueries({
        @NamedQuery(name = "MeshtermCategory.findAll", query = "SELECT m FROM MeshtermCategory m"),
        @NamedQuery(name = "MeshtermCategory.findByCategoryId", query = "SELECT m FROM MeshtermCategory m WHERE m.categoryId = :categoryId"),
        @NamedQuery(name = "MeshtermCategory.findByName", query = "SELECT m FROM MeshtermCategory m WHERE m.name = :name")})
public class MeshtermCategory implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @Basic(optional = false)
    @Column(name = "category_id", nullable = false, length = 20)
    private String categoryId;
    @Column(name = "name", length = 128)
    private String name;

    public MeshtermCategory() {
    }

    public MeshtermCategory(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(String categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        int hash = 0;
        hash += (categoryId != null ? categoryId.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object object) {
        // TODO: Warning - this method won't work in the case the id fields are not set
        if (!(object instanceof MeshtermCategory)) {
            return false;
        }
        MeshtermCategory other = (MeshtermCategory) object;
        if ((this.categoryId == null && other.categoryId != null) || (this.categoryId != null && !this.categoryId.equals(other.categoryId))) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "com.cscie599.gfn.entities.MeshtermCategory[ categoryId=" + categoryId + " ]";
    }

}
