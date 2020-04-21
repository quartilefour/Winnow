package com.cscie599.gfn.importer.analyzer;

import com.cscie599.gfn.importer.CacheableEntity;

import java.util.concurrent.atomic.AtomicLong;

/**
 * A POJO representation of the input object for Enrichment analysis of Gene and Meshterms.
 * This is a record of an association between a gene and Meshterm via a publication.
 *
 * @author PulkitBhanot
 */
public class GeneMeshPub implements CacheableEntity {

    private String geneId;
    private String publicationId;
    private String meshId;

    // Default initialize with a value of 1 as this object is only going to be created when there is a pair of gene mesh in some publication
    private AtomicLong counter = new AtomicLong(1);

    public GeneMeshPub() {
    }

    public GeneMeshPub(String geneId, String publicationId, String meshId) {
        this.geneId = geneId;
        this.publicationId = publicationId;
        this.meshId = meshId;
    }

    public String getGeneId() {
        return geneId;
    }

    public void setGeneId(String geneId) {
        this.geneId = geneId;
    }

    public String getPublicationId() {
        return publicationId;
    }

    public void setPublicationId(String publicationId) {
        this.publicationId = publicationId;
    }

    public String getMeshId() {
        return meshId;
    }

    public void setMeshId(String meshId) {
        this.meshId = meshId;
    }

    public String getKey() {
        return new StringBuilder().append(geneId).append("-").append(meshId).toString();
    }

    public AtomicLong getCounter() {
        return counter;
    }

    @Override
    public String toString() {
        return "GeneMeshPub{" +
                "geneId='" + geneId + '\'' +
                ", publicationId='" + publicationId + '\'' +
                ", meshId='" + meshId + '\'' +
                ", counter=" + counter.get() +
                '}';
    }
}
