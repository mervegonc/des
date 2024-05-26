package com.tobias.des.dto.responses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.tobias.des.entity.Post;

import lombok.Data;

@Data
public class PostResponse {

	Long id;
	Long userId;
	String userName;
	String title;
	String text;
	Date createdAt;
	String formattedCreatedAt;
	List<LikeResponse> postLikes;

	public PostResponse(Post entity, List<LikeResponse> likes) {
		this.id = entity.getId();
		this.userId = entity.getUser().getId();
		this.userName = entity.getUser().getUsername();
		this.title = entity.getTitle();
		this.text = entity.getText();
		this.createdAt = entity.getCreatedAt();
		this.formattedCreatedAt = formatDate(this.createdAt);
		this.postLikes = likes;

	}

	private String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return formatter.format(date);
	}
}
