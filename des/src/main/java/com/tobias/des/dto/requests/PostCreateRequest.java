package com.tobias.des.dto.requests;

import lombok.Data;

@Data
public class PostCreateRequest {

	Long id;
	String text;
	String title;
	Long userId;
	private Long articleId;
	private String connections;
}