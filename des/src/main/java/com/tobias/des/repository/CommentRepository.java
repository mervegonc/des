package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tobias.des.entity.Comment;

@Repository
public interface CommentRepository extends JpaRepository<Comment, Long> {

	List<Comment> findByUserIdAndPostId(Long userId, Long postId);

	List<Comment> findByUserId(Long userId);

	/*
	 * @Query("SELECT c FROM Comment c WHERE c.postId = ?1") List<Comment>
	 * findCommentsByPostId(Long postId);
	 */

	List<Comment> findByPostId(Long postId);

	void deleteByUserIdAndPostIdAndId(Long userId, Long postId, Long commentId);

	@Override
	List<Comment> findAll(Sort sort);
}
