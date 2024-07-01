package com.tobias.des.dto.responses;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import com.tobias.des.entity.Post;
import com.tobias.des.entity.PostPhotos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PostResponse {
	Long id;
	Long userId;
	String userName;
	String title;
	String text;
	String connections;
	Date createdAt;
	String formattedCreatedAt;
	List<LikeResponse> likes;
	Long articleId;
	String articleSubject; // Yeni eklenen alan
	List<String> photoUrls;

	public PostResponse(Post post, List<LikeResponse> likes) {
		this.id = post.getId();
		this.userId = post.getUser().getId();
		this.userName = post.getUser().getUsername();
		this.title = post.getTitle();
		this.text = post.getText();
		this.connections = post.getConnections();
		this.createdAt = post.getCreatedAt();
		this.formattedCreatedAt = post.getFormattedCreatedAt();
		this.likes = likes;
		this.articleId = post.getArticleId();
		this.articleSubject = post.getArticle() != null ? post.getArticle().getSubject() : null; // Yeni eklenen alan
		this.photoUrls = post.getPhotos() != null
				? post.getPhotos().stream().map(PostPhotos::getPhotoName).collect(Collectors.toList())
				: null;
	}
}
