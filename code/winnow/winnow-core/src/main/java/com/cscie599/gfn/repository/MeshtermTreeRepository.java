package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.MeshtermTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface    MeshtermTreeRepository extends JpaRepository<MeshtermTree, String> {
    List<MeshtermTree> findByTreeNodeId(String treeNodeId);
    @Query(nativeQuery = true, value = "SELECT * FROM meshterm_tree m WHERE m.tree_parent_id = :treeParentId LIMIT 1")
    MeshtermTree findOneByTreeParentId(@Param("treeParentId") String treeParentId);
    @Query("SELECT m FROM MeshtermTree m WHERE m.meshtermTreePK.treeNodeId like :treeNodeId% ORDER BY m.meshtermTreePK.treeParentId, m.meshtermTreePK.treeNodeId")
    List<MeshtermTree> findByTreeNodeIdStartingWithOrderByMeshtermTreePK(@Param("treeNodeId") String treeNodeId);
    @Query("SELECT m FROM MeshtermTree m WHERE m.meshtermTreePK.treeParentId = :treeParentId ORDER BY m.meshtermTreePK.treeNodeId")
    List<MeshtermTree> findByTreeParentIdOrderByMeshtermTreePK(@Param("treeParentId") String treeParentId);
    @Query(value = "select mt.mesh_id,mt.tree_parent_id, mt.tree_node_id, m.name, mt.tree_parent_id || '.' || mt.tree_node_id as full_parent_id from meshterm_tree mt join meshterm m on m.mesh_id = mt.mesh_id order by mt.tree_parent_id asc", nativeQuery = true)
    Object[][] findAllSortedByParentId();
}
