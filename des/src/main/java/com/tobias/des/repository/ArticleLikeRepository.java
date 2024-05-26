package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.tobias.des.entity.ArticleLike;

@Repository
public interface ArticleLikeRepository extends JpaRepository<ArticleLike, Long> {

	List<ArticleLike> findByUserIdAndArticleId(Long userId, Long articleId);

	List<ArticleLike> findByUserId(Long userId);

	List<ArticleLike> findByArticleId(Long articleId);

	boolean existsByUserIdAndArticleId(Long userId, Long articleId);

	@Query("SELECT l.id FROM ArticleLike l WHERE l.user.id = :userId AND l.article.id = :articleId")
	Long findIdByUserIdAndArticleId(@Param("userId") Long userId, @Param("articleId") Long articleId);

	@Modifying
	@Query("DELETE FROM ArticleLike l WHERE l.user.id = :userId AND l.article.id = :articleId")
	void deleteById(@Param("userId") Long userId, @Param("articleId") Long articleId);

}
