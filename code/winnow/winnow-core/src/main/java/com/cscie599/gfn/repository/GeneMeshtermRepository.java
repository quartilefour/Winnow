package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.GeneMeshterm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneMeshtermRepository extends JpaRepository<GeneMeshterm, String> {

    @Query(nativeQuery = true, value = "SELECT * FROM gene_meshterm gm WHERE gm.mesh_id IN (:meshIds) ORDER BY gm.p_value LIMIT :limit OFFSET :offset")
    List<GeneMeshterm> findByMeshIdsOrderByPValue(List<String> meshIds, long offset, int limit);

    @Query(nativeQuery = true, value = "SELECT * FROM gene_meshterm gm WHERE gm.gene_id IN (:geneIds) ORDER BY gm.p_value LIMIT :limit  OFFSET :offset")
    List<GeneMeshterm> findByGeneIdsOrderByPValue(List<String> geneIds, long offset, int limit);

    @Query(nativeQuery = true, value = "SELECT * FROM gene_meshterm gm WHERE gm.gene_id = :geneId ORDER BY gm.p_value LIMIT :limit  OFFSET :offset")
    List<GeneMeshterm> findByGeneIdOrderByPValue(@Param("geneId") String geneId, long offset, int limit);

    @Query(nativeQuery = true, value = "SELECT * FROM gene_meshterm gm " +
            "WHERE gm.gene_id IN (:geneIds) AND gm.mesh_id IN (:meshIds) " +
            "ORDER BY gm.p_value LIMIT :limit OFFSET :offset")
    List<GeneMeshterm> findByGeneIdsAndMeshIdsOrderByPValue(List<String> geneIds, List<String> meshIds, long offset, int limit);
}
