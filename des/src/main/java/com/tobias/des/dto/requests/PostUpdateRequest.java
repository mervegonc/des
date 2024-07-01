package com.tobias.des.dto.requests;

import lombok.Data;

@Data
public class PostUpdateRequest {
	String title;
	String text;
	Long articleId;
	private String connections;
}