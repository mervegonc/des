package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tobias.des.entity.ArticleComment;

@Repository
public interface ArticleCommentRepository extends JpaRepository<ArticleComment, Long> {

	List<ArticleComment> findByUserIdAndArticleId(Long userId, Long articleId);

	List<ArticleComment> findByUserId(Long userId);

	/*
	 * @Query("SELECT c FROM Comment c WHERE c.postId = ?1") List<Comment>
	 * findCommentsByPostId(Long postId);
	 */

	List<ArticleComment> findByArticleId(Long articleId);

	void deleteByUserIdAndArticleIdAndId(Long userId, Long articleId, Long articleCommentId);

	@Override
	List<ArticleComment> findAll(Sort sort);
}
