package com.tobias.des.dto.responses;

import com.tobias.des.entity.Article;

import lombok.Data;

@Data
public class ArticleResponse {

	Long id;
	Long userId;
	String subject;
	String content;

	public ArticleResponse(Article entity) {
		this.id = entity.getId();
		this.userId = entity.getUser().getId();
		this.subject = entity.getSubject();
		this.content = entity.getContent();

	}
}
