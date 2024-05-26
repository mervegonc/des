package com.tobias.des.dto.requests;

import lombok.Data;

@Data
public class ArticleLikeCreateRequest {
	Long id;
	Long userId;
	Long articleId;
}
