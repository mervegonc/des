package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tobias.des.entity.ArticlePhotos;
import com.tobias.des.entity.PostPhotos;

@Repository
public interface ArticlePhotosRepository extends JpaRepository<PostPhotos, Long> {

	List<PostPhotos> findByPostId(Long postId);
	// Burada PostPhotos entity'si için özel metotlar tanımlanabilir

	void save(ArticlePhotos articlePhoto);

}
