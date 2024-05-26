package com.tobias.des.dto.responses;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.tobias.des.entity.ArticleLike;

@JsonIgnoreProperties
public class ArticleLikeResponse {
	Long id;
	Long userId;
	Long articleId;

	public ArticleLikeResponse(ArticleLike entity) {
		this.id = entity.getId();
		this.userId = entity.getUser().getId();
		this.articleId = entity.getArticle().getId();
	}

}
