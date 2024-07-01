package com.tobias.des.dto.requests;

import lombok.Data;

@Data
public class ArticleUpdateRequest {

	String subject;
	String content;
	String connections;
}
