package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.Search;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SearchRepository extends JpaRepository<Search, String> {

    List<Search> findBySearchId(String searchId);

    @Query("SELECT s FROM Search s WHERE s.createdBy.userEmail = :userEmail ORDER BY s.searchId DESC")
    List<Search> findByCreatedByOrderBySearchId(String userEmail);

    @Query("SELECT s FROM Search s WHERE s.createdBy.userEmail = :userEmail AND s.searchId = :searchId")
    Search findByCreatedByAndSearchId(String userEmail, long searchId);
}
