package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.tobias.des.entity.Post;
import com.tobias.des.entity.PostPhotos;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {

	List<Post> findByUserId(Long userId);

	@Query("SELECT p FROM Post p WHERE p.text LIKE %:keyword%")
	List<Post> findByContentContaining(String keyword);

	List<PostPhotos> findPhotosById(Long postId);
}