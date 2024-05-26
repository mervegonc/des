package com.tobias.des.dto.responses;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.tobias.des.entity.ArticleComment;

import lombok.Data;

@Data
public class ArticleCommentResponse {
	Long id;
	Long userId;
	Long articleId;
	String text;
	Date createdAt;
	String formattedCreatedAt;

	public ArticleCommentResponse(ArticleComment entity) {
		this.id = entity.getId();
		this.userId = entity.getUser().getId();
		this.articleId = entity.getArticle().getId();
		this.text = entity.getText();
		this.createdAt = entity.getCreatedAt();
		this.formattedCreatedAt = formatDate(this.createdAt);

	}

	private String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return formatter.format(date);
	}
}
