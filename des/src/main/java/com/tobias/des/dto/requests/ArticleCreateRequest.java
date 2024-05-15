package com.tobias.des.dto.requests;

import lombok.Data;

@Data
public class ArticleCreateRequest {

	Long id;
	String content;
	String subject;
	Long userId;

}
