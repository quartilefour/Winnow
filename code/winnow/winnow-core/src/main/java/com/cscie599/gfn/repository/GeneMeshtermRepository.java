package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.GeneMeshterm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneMeshtermRepository extends JpaRepository<GeneMeshterm, String> {

    @Query("SELECT gm FROM GeneMeshterm gm WHERE gm.geneMeshtermPK.meshId IN (:meshIds) ORDER BY gm.pValue")
    List<GeneMeshterm> findByMeshIdsOrderByPValue(List<String> meshIds);

    @Query("SELECT gm FROM GeneMeshterm gm WHERE gm.geneMeshtermPK.geneId IN (:geneIds) ORDER BY gm.pValue")
    List<GeneMeshterm> findByGeneIdsOrderByPValue(List<String> geneIds);

    @Query("SELECT gm FROM GeneMeshterm gm WHERE gm.geneMeshtermPK.geneId = :geneId ORDER BY gm.pValue")
    List<GeneMeshterm> findByGeneIdOrderByPValue(@Param("geneId") String geneId);

    @Query(nativeQuery = true, value = "SELECT * FROM gene_meshterm gm " +
            "WHERE gm.gene_id IN (:geneIds) AND gm.mesh_id IN (:meshIds) " +
            "ORDER BY gm.p_value LIMIT 100")
    List<GeneMeshterm> findByGeneIdsAndMeshIdsOrderByPValue(List<String> geneIds, List<String> meshIds);
}
