package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tobias.des.entity.Like;

@Repository
public interface LikeRepository extends JpaRepository<Like, Long> {

	List<Like> findByUserIdAndPostId(Long userId, Long postId);

	List<Like> findByUserId(Long userId);

	List<Like> findByPostId(Long postId);

	boolean existsByUserIdAndPostId(Long userId, Long postId);

	@Query("SELECT l.id FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
	Long findIdByUserIdAndPostId(@Param("userId") Long userId, @Param("postId") Long postId);

	@Modifying
	@Query("DELETE FROM Like l WHERE l.user.id = :userId AND l.post.id = :postId")
	void deleteById(@Param("userId") Long userId, @Param("postId") Long postId);

}
