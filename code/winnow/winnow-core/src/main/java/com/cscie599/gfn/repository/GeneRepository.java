package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.Gene;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface GeneRepository extends JpaRepository<Gene, String> {

    List<Gene> findByGeneId(String geneId);
    List<Gene> findAll();
    @Query("SELECT g FROM Gene g WHERE (LOWER(g.geneId) LIKE %:pattern% OR LOWER(g.symbol) LIKE %:pattern% OR LOWER(g.description) LIKE %:pattern%) AND g.symbol != 'NEWENTRY'")
    List<Gene> findAllContaining(@Param("pattern") String pattern);
}
