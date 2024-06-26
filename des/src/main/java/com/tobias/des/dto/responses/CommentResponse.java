package com.tobias.des.dto.responses;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tobias.des.entity.Comment;

import lombok.Data;

@Data
public class CommentResponse {
	Long id;
	Long userId;
	Long postId;
	String text;
	Date createdAt;
	String formattedCreatedAt;

	public CommentResponse(Comment entity) {
		this.id = entity.getId();
		this.userId = entity.getUser().getId();
		this.postId = entity.getPost().getId();
		this.text = entity.getText();
		this.createdAt = entity.getCreatedAt();
		this.formattedCreatedAt = formatDate(this.createdAt);

	}

	private String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return formatter.format(date);
	}
}
