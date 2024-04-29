package com.tobias.des.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.tobias.des.entity.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsernameOrEmail(String username, String email);

	@Override
	User save(User user);

	@Query("SELECT u FROM User u WHERE u.username LIKE %:username%")
	List<User> searchFindByUsername(String username);

	// Kullanıcı adını kontrol etmek için metot
	boolean existsByUsername(String username);

	User findByUsername(String username);

	Long getUserIdByUsernameOrEmail(String username, String email);

}
