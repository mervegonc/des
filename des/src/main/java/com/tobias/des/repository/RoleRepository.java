package com.tobias.des.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobias.des.entity.Role;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Role findByName(String name);
}