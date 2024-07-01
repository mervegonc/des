package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tobias.des.entity.Connection;

public interface ConnectionRepository extends JpaRepository<Connection, Long> {
	List<Connection> findByUserId(Long userId);
}
