package com.tobias.des.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.tobias.des.entity.PostVideos;

@Repository
public interface PostVideosRepository extends JpaRepository<PostVideos, Long> {
	List<PostVideos> findByPostId(Long postId);
}
