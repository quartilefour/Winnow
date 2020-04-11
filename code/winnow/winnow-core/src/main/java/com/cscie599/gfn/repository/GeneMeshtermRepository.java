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

    @Query("SELECT gm FROM GeneMeshterm gm INNER JOIN gm.meshterm m WHERE m.name IN (:meshNames) ORDER BY gm.pValue")
    List<GeneMeshterm> findByMeshNamesOrderByPValue(List<String> meshNames);

    @Query(nativeQuery = true, value = "SELECT * FROM gene_meshterm gm " +
            "INNER JOIN meshterm_tree mt ON gm.mesh_id = mt.mesh_id " +
            "WHERE (CONCAT(tree_parent_id, '.', tree_node_id)) IN (:meshTreeIds) " +
            "ORDER BY gm.p_value")
    List<GeneMeshterm> findByMeshTreeIdsOrderByPValue(List<String> meshTreeIds);

    @Query("SELECT gm FROM GeneMeshterm gm WHERE gm.geneMeshtermPK.geneId IN (:geneIds) ORDER BY gm.pValue")
    List<GeneMeshterm> findByGeneIdsOrderByPValue(List<String> geneIds);

    @Query("SELECT gm FROM GeneMeshterm gm INNER JOIN gm.gene g WHERE g.symbol IN (:geneSymbols) ORDER BY gm.pValue")
    List<GeneMeshterm> findByGeneSymbolsOrderByPValue(List<String> geneSymbols);

    @Query("SELECT gm FROM GeneMeshterm gm WHERE gm.geneMeshtermPK.geneId = :geneId ORDER BY gm.pValue")
    List<GeneMeshterm> findByGeneIdOrderByPValue(@Param("geneId") String geneId);

}
