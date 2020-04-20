package com.cscie599.gfn.importer.analyzer;

import com.cscie599.gfn.importer.CacheableEntity;

import java.util.Objects;

/**
 * @author PulkitBhanot
 */
public class GeneRawStats implements CacheableEntity {

    private long publicationsWithoutGene;

    private long publicationsWithGene;

    private String geneId;

    public long getPublicationsWithoutGene() {
        return publicationsWithoutGene;
    }

    public void setPublicationsWithoutGene(long publicationsWithoutGene) {
        this.publicationsWithoutGene = publicationsWithoutGene;
    }

    public long getPublicationsWithGene() {
        return publicationsWithGene;
    }

    public void setPublicationsWithGene(long publicationsWithGene) {
        this.publicationsWithGene = publicationsWithGene;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        GeneRawStats that = (GeneRawStats) o;
        return getGeneId().equals(that.getGeneId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getGeneId());
    }

    @Override
    public String toString() {
        return "GeneRawStats{" +
                "publicationsWithoutGene=" + publicationsWithoutGene +
                ", getPublicationsWithGene=" + publicationsWithGene +
                ", geneId='" + geneId + '\'' +
                '}';
    }

    @Override
    public String getKey() {
        return geneId;
    }
}
