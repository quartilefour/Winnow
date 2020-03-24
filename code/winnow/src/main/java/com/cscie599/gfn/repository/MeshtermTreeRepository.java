package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.MeshtermTree;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeshtermTreeRepository extends JpaRepository<MeshtermTree, String> {

    List<MeshtermTree> findByMeshId(String meshId);
    List<MeshtermTree> findByTreeParentId(String treeParentId);
    List<MeshtermTree> findByTreeNodeId(String treeNodeId);
    List<MeshtermTree> findAll();
    @Query("SELECT m FROM MeshtermTree m WHERE m.meshtermTreePK.treeNodeId like %:treeNodeId%")
    List<MeshtermTree> findByTreeNodeIdStartingWith(@Param("treeNodeId") String treeNodeId);
}
