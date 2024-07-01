package com.tobias.des.dto.responses;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import com.tobias.des.entity.Article;

import lombok.Data;

@Data
public class ArticleResponse {

	Long id;
	Long userId;
	String userName;
	String subject;
	String content;
	Date createdAt;
	String formattedCreatedAt;
	List<ArticleLikeResponse> articleLikes;
	String connections;

	public ArticleResponse(Article entity, List<ArticleLikeResponse> likes) {
		this.id = entity.getId();
		this.userId = entity.getUser().getId();
		this.userName = entity.getUser().getUsername();
		this.subject = entity.getSubject();
		this.content = entity.getContent();
		this.createdAt = entity.getCreatedAt();
		this.formattedCreatedAt = formatDate(this.createdAt);
		this.articleLikes = likes;
		this.connections = entity.getConnections();
	}

	private String formatDate(Date date) {
		SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm");
		return formatter.format(date);
	}

}
