package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.MeshtermCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MeshtermCategoryRepository extends JpaRepository<MeshtermCategory, String> {

    List<MeshtermCategory> findByCategoryId(String categoryId);
    List<MeshtermCategory> findAll();
}
