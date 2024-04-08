package com.tobias.des.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.context.annotation.Lazy;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tobias.des.dto.requests.LikeCreateRequest;
import com.tobias.des.dto.responses.LikeResponse;
import com.tobias.des.entity.Like;
import com.tobias.des.service.LikeService;

@RestController
@RequestMapping("/api/like")
public class LikeController {
	private LikeService likeService;

	public LikeController(@Lazy LikeService likeService) {
		this.likeService = likeService;
	}

	@GetMapping("/likeId/{postId}/user/{userId}")
	public Long getLikeIdByUserIdAndPostId(@PathVariable Long postId, @PathVariable Long userId) {
		return likeService.getLikeIdByUserIdAndPostId(userId, postId);
	}

	@GetMapping("/{postId}/user/{userId}")
	public boolean hasUserLikedPost(@PathVariable Long postId, @PathVariable Long userId) {
		return likeService.hasUserLikedPost(userId, postId);
	}

	@GetMapping("/count/{postId}")
	public Long getTotalLikesForPostId(@PathVariable Long postId) {
		return likeService.getTotalLikesForPostId(postId);
	}

	@GetMapping("/get/{postId}")
	public List<LikeResponse> getAllLikesForPostId(@RequestParam Optional<Long> postId) {
		return likeService.getAllLikesForPostId(postId);
	}

	@GetMapping
	public List<LikeResponse> getAllLikes(@RequestParam Optional<Long> userId, @RequestParam Optional<Long> postId) {
		return likeService.getAllLikesWithParam(userId, postId);
	}

	@PostMapping
	public Like createOneLike(@RequestBody LikeCreateRequest request) {
		return likeService.createOneLike(request);
	}

	@GetMapping("/{likeId}")
	public Like getOneLike(@PathVariable Long likeId) {
		return likeService.getOneLikeById(likeId);
	}

	@DeleteMapping("/unlike/{userId}/{postId}")
	public void deleteOneLike(@PathVariable Long userId, @PathVariable Long postId) {
		likeService.deleteOneLikeById(userId, postId);
	}
}
