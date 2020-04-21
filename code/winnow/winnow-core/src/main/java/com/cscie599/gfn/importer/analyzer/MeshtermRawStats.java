package com.cscie599.gfn.importer.analyzer;

import com.cscie599.gfn.importer.CacheableEntity;

import java.util.Objects;

/**
 * A POJO representation of the input object for Enrichment analysis of Meshterm.
 *
 * @author PulkitBhanot
 */
public class MeshtermRawStats implements CacheableEntity {

    private long publicationsWithoutTerm;

    private long publicationsWithTerm;

    private String meshId;

    public MeshtermRawStats() {
    }

    public MeshtermRawStats(long publicationsWithoutTerm, long publicationsWithTerm, String meshId) {
        this.publicationsWithoutTerm = publicationsWithoutTerm;
        this.publicationsWithTerm = publicationsWithTerm;
        this.meshId = meshId;
    }

    public long getPublicationsWithoutTerm() {
        return publicationsWithoutTerm;
    }

    public void setPublicationsWithoutTerm(long publicationsWithoutTerm) {
        this.publicationsWithoutTerm = publicationsWithoutTerm;
    }

    public long getPublicationsWithTerm() {
        return publicationsWithTerm;
    }

    public void setPublicationsWithTerm(long publicationsWithTerm) {
        this.publicationsWithTerm = publicationsWithTerm;
    }

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MeshtermRawStats that = (MeshtermRawStats) o;
        return getMeshId().equals(that.getMeshId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getMeshId());
    }

    @Override
    public String getKey() {
        return meshId;
    }

    @Override
    public String toString() {
        return "MeshtermRawStats{" +
                "publicationsWithoutTerm=" + publicationsWithoutTerm +
                ", publicationsWithTerm=" + publicationsWithTerm +
                ", meshId='" + meshId + '\'' +
                '}';
    }
}
