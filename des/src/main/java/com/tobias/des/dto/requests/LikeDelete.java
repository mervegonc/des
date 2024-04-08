package com.tobias.des.dto.requests;

import lombok.Data;

@Data
public class LikeDelete {
	Long id;
	Long userId;
	Long postId;
}
