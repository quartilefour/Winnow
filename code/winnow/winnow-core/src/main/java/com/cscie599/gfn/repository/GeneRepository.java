package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.Gene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneRepository extends JpaRepository<Gene, String> {

    List<Gene> findByGeneId(String geneId);
    List<Gene> findAll();
}
