package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tobias.des.entity.ArticlePhotos;

@Repository
public interface ArticlePhotosRepository extends JpaRepository<ArticlePhotos, Long> {

	List<ArticlePhotos> findByArticleId(Long articleId);
	// Burada PostPhotos entity'si için özel metotlar tanımlanabilir

	void deleteByPhotoName(String photoName);

}
