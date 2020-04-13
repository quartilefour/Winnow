package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.Meshterm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeshtermRepository extends JpaRepository<Meshterm, String> {

    List<Meshterm> findByMeshId(String meshId);

    List<Meshterm> findAll();

    @Query(nativeQuery = true, value = "SELECT m.mesh_id FROM meshterm m " +
            "INNER JOIN meshterm_tree mt ON m.mesh_id = mt.mesh_id " +
            "WHERE m.mesh_id IN (:meshIds) OR m.name IN (:names) " +
            "OR (CONCAT(mt.tree_parent_id, '.', mt.tree_node_id)) IN (:meshTreeIds)")
    List<String> findMeshIdsByMeshIdsOrNamesOrMeshTreeIds(List<String> meshIds, List<String> names, List<String> meshTreeIds);
}
