package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserRepository extends JpaRepository<User, String> {
    User findByUserEmail(String userEmail);
}
