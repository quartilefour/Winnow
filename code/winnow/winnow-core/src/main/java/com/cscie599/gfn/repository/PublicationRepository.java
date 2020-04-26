package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, String> {

    @Query(nativeQuery = true, value ="SELECT * FROM publication p " +
            "INNER JOIN gene_publication gp ON p.publication_id = gp.publication_id " +
            "INNER JOIN publication_meshterm pm ON p.publication_id = pm.publication_id " +
            "WHERE gp.gene_id IN (:geneId, (SELECT other_gene_id FROM gene_gene WHERE gene_id = :geneId)) " +
            "AND pm.mesh_id = :meshId")
    List<Publication> findByGeneIdAndMeshId(String geneId, String meshId);

}
