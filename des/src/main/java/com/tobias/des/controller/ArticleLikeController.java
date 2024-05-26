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

import com.tobias.des.dto.requests.ArticleLikeCreateRequest;
import com.tobias.des.dto.responses.ArticleLikeResponse;
import com.tobias.des.entity.ArticleLike;
import com.tobias.des.service.ArticleLikeService;

@RestController
@RequestMapping("/api/articlelike")
public class ArticleLikeController {

	private ArticleLikeService articleLikeService;

	public ArticleLikeController(@Lazy ArticleLikeService articleLikeService) {
		this.articleLikeService = articleLikeService;
	}

	@GetMapping("/likeId/{articleId}/user/{userId}")
	public Long getLikeIdByUserIdAndArticleId(@PathVariable Long articleId, @PathVariable Long userId) {
		return articleLikeService.getLikeIdByUserIdAndArticleId(userId, articleId);
	}

	@GetMapping("/{articleId}/user/{userId}")
	public boolean hasUserLikedArticle(@PathVariable Long articleId, @PathVariable Long userId) {
		return articleLikeService.hasUserLikedArticle(userId, articleId);
	}

	@GetMapping("/count/{articleId}")
	public Long getTotalLikesForPostId(@PathVariable Long articleId) {
		return articleLikeService.getTotalLikesForArticleId(articleId);
	}

	@GetMapping("/get/{articleId}")
	public List<ArticleLikeResponse> getAllLikesForArticleId(@RequestParam Optional<Long> articleId) {
		return articleLikeService.getAllLikesForArticleId(articleId);
	}

	@GetMapping
	public List<ArticleLikeResponse> getAllLikes(@RequestParam Optional<Long> userId,
			@RequestParam Optional<Long> articleId) {
		return articleLikeService.getAllArticleLikesWithParam(userId, articleId);
	}

	@PostMapping
	public ArticleLike createOneLike(@RequestBody ArticleLikeCreateRequest request) {
		return articleLikeService.createOneLike(request);
	}

	@GetMapping("/{likeId}")
	public ArticleLike getOneLike(@PathVariable Long likeId) {
		return articleLikeService.getOneArticleLikeById(likeId);
	}

	@DeleteMapping("/unlike/{userId}/{articleId}")
	public void deleteOneLike(@PathVariable Long userId, @PathVariable Long articleId) {
		articleLikeService.deleteOneArticleLikeById(userId, articleId);
	}
}
