package com.tobias.des.dto.requests;

import lombok.Data;

@Data
public class ArticleCommentCreateRequest {
	Long id;
	Long userId;
	Long articleId;
	String text;
}
