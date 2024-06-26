package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tobias.des.entity.Article;
import com.tobias.des.entity.ArticlePhotos;
import com.tobias.des.entity.User;

@Repository
public interface ArticleRepository extends JpaRepository<Article, Long> {
	@Query("SELECT a FROM Article a WHERE a.content LIKE %:keyword%")
	List<Article> findByContentContaining(String keyword);

	List<Article> findByUser(User user);

	List<Article> findByUserId(Long userId);

	List<ArticlePhotos> findArticleById(Long articleId);

	@Override
	List<Article> findAll(Sort sort);
}
