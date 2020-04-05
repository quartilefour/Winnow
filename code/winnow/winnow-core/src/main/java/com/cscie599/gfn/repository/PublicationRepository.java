package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.Publication;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PublicationRepository extends JpaRepository<Publication, String> {

    @Query("SELECT p FROM Publication p " +
            "INNER JOIN GenePublication gp on p.publicationId = gp.genePublicationPK.publicationId " +
            "INNER JOIN PublicationMeshterm pm on p.publicationId = pm.publicationMeshtermPK.publicationId " +
            "WHERE gp.genePublicationPK.geneId = :geneId AND pm.publicationMeshtermPK.meshId = :meshId")
    List<Publication> findByGeneIdAndMeshId(String geneId, String meshId);

}
