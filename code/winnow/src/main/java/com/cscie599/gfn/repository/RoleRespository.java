package com.cscie599.gfn.repository;

import com.cscie599.gfn.entities.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, Long>{
}