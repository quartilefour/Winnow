package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.Meshterm;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeshtermRepository extends JpaRepository<Meshterm, String> {

    List<Meshterm> findByMeshId(String meshId);
    List<Meshterm> findAll();
}
