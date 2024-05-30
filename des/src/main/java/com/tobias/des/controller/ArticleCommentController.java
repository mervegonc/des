package com.tobias.des.controller;

import java.util.List;
import java.util.Optional;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tobias.des.dto.requests.ArticleCommentCreateRequest;
import com.tobias.des.dto.requests.ArticleCommentUpdateRequest;
import com.tobias.des.dto.responses.ArticleCommentResponse;
import com.tobias.des.entity.ArticleComment;
import com.tobias.des.service.ArticleCommentService;

@RestController
@RequestMapping("/api/articlecomment")
public class ArticleCommentController {

	private ArticleCommentService articleCommentService;

	public ArticleCommentController(ArticleCommentService articleCommentService) {
		super();
		this.articleCommentService = articleCommentService;
	}

	@GetMapping
	public List<ArticleComment> getAllComments(@RequestParam Optional<Long> userId,
			@RequestParam Optional<Long> articleId) {
		return articleCommentService.getAllCommentsWithParam(userId, articleId);
	}

	@PostMapping
	public ArticleComment createOneComment(@RequestBody ArticleCommentCreateRequest request) {
		return articleCommentService.createOneComment(request);
	}

	@GetMapping("/{articleCommentId}")
	public ArticleComment getOneComment(@PathVariable Long articleCommentId) {
		return articleCommentService.getOneCommentById(articleCommentId);
	}

	@PutMapping("/{articleCommentId}")
	public ArticleComment updateOneComment(@PathVariable Long articleCommentId,
			@RequestBody ArticleCommentUpdateRequest request) {
		return articleCommentService.updateOneCommentById(articleCommentId, request);
	}

	@DeleteMapping("/{userId}/{articleId}/{articleCommentId}")
	public void deleteComment(@PathVariable Long userId, @PathVariable Long articleId,
			@PathVariable Long articleCommentId) throws Exception {
		articleCommentService.deleteArticleComment(userId, articleId, articleCommentId);
	}

	@GetMapping("/article/{articleId}/articleComment")
	public List<ArticleCommentResponse> usersCommentsByArticleId(@PathVariable Long articleId) {
		return articleCommentService.getCommentsResponseByArticleId(articleId);
	}

}
