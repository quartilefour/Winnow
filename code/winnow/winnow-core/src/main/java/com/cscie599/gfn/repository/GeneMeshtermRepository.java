package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.GeneMeshterm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneMeshtermRepository extends JpaRepository<GeneMeshterm, String> {

    @Query("SELECT gm FROM GeneMeshterm gm WHERE gm.geneMeshtermPK.meshId in (:meshIds)")
    List<GeneMeshterm> findByMeshIds(List<String> meshIds);

    @Query("SELECT gm FROM GeneMeshterm gm inner join gm.meshterm m WHERE m.name in (:meshNames)")
    List<GeneMeshterm> findByMeshNames(List<String> meshNames);

    @Query(nativeQuery = true, value = "SELECT * FROM gene_meshterm gm inner join meshterm_tree mt on " +
            "gm.mesh_id=mt.mesh_id WHERE (CONCAT(tree_parent_id, '.', tree_node_id)) in (:meshTreeIds)")
    List<GeneMeshterm> findByMeshTreeIds(List<String> meshTreeIds);

    @Query("SELECT gm FROM GeneMeshterm gm WHERE gm.geneMeshtermPK.geneId in (:geneIds)")
    List<GeneMeshterm> findByGeneIds(List<String> geneIds);

    @Query("SELECT gm FROM GeneMeshterm gm inner join gm.gene g WHERE g.symbol in (:geneSymbols)")
    List<GeneMeshterm> findByGeneSymbols(List<String> geneSymbols);


    List<GeneMeshterm> findAll();
}