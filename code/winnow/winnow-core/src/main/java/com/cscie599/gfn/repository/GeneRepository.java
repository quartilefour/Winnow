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

    @Query(nativeQuery = true, value = "SELECT * FROM gene g " +
            "WHERE g.tax_id = 9606 " +
            "AND (LOWER(g.gene_id) LIKE %:pattern% OR LOWER(g.symbol) LIKE %:pattern% OR LOWER(g.description) LIKE %:pattern%) " +
            "AND g.symbol NOT LIKE 'NEWENTRY' " +
            "ORDER BY g.gene_id LIMIT 25")
    List<Gene> findAllContaining(@Param("pattern") String pattern);

    @Query(nativeQuery = true, value = "SELECT gene_id FROM gene g WHERE g.gene_id IN (:geneIds) OR g.symbol IN (:symbols) OR g.description IN (:descriptions)")
    List<String> findGeneIdsByGeneIdsOrSymbolsOrDescriptions(List<String> geneIds, List<String> symbols, List<String> descriptions);

    @Query(nativeQuery = true, value = "SELECT gene_id, COUNT(*) FROM gene_publication WHERE publication_id in " +
            "(SELECT publication_id FROM gene_publication WHERE gene_id = :geneId) AND gene_id != :geneId " +
            "GROUP BY gene_id ORDER BY COUNT DESC LIMIT 100")
    List<Object[]> findCoOccurringGeneIdsAndCountsByGeneIdOrderByCounts(@Param("geneId") String geneId);
}
