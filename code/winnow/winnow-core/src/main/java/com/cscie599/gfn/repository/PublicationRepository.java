package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, String> {
    @Query(nativeQuery = true, value ="SELECT DISTINCT ON (p.publication_id) " +
            "p.publication_id, p.completed_date, p.date_revised, p.title FROM publication p " +
            "INNER JOIN gene_publication gp ON p.publication_id = gp.publication_id " +
            "INNER JOIN publication_meshterm pm ON p.publication_id = pm.publication_id " +
            "WHERE gp.gene_id IN ((select other_gene_id from gene_gene where gene_id = :geneId) " +
            "UNION DISTINCT (select gene_id from gene_gene where other_gene_id = :geneId)" +
            "UNION DISTINCT (select gene_id from gene where gene_id = :geneId)) AND pm.mesh_id = :meshId")
    List<Publication> findByGeneIdAndMeshId(String geneId, String meshId);

}
