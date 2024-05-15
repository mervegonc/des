package com.tobias.des.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tobias.des.dto.requests.ArticleCreateRequest;
import com.tobias.des.dto.requests.ArticleUpdateRequest;
import com.tobias.des.dto.responses.ArticleResponse;
import com.tobias.des.entity.Article;
import com.tobias.des.service.ArticleService;

@RestController
@RequestMapping("/api/articles")
public class ArticleController {
	private final ArticleService articleService;

	@Autowired
	public ArticleController(ArticleService articleService) {
		this.articleService = articleService;
	}

	@PostMapping("/create")
	public ResponseEntity<Article> createArticle(@RequestBody ArticleCreateRequest request) {
		Article article = articleService.createArticle(request.getUserId(), request.getSubject(), request.getContent());
		return new ResponseEntity<>(article, HttpStatus.CREATED);
	}

	@GetMapping
	public List<ArticleResponse> getAllArticles() {
		return articleService.getAllArticles();
	}

	@GetMapping("/{userId}")
	public List<ArticleResponse> getAllArticlesByUserId(@PathVariable Long userId) {
		return articleService.getAllArticlesByUserId(userId);
	}

	@PutMapping("/{articleId}")
	public Article updateOneArticlesById(@PathVariable Long articleId,
			@RequestBody ArticleUpdateRequest updateArticle) {
		return articleService.updateOneArticleById(articleId, updateArticle);
	}

	@DeleteMapping("/{articleId}")
	public void deleteOnearticleById(@PathVariable Long articleId) throws IOException {
		// Postu sil
		articleService.deleteOneArticleById(articleId);
	}

	@GetMapping("/search")
	public List<Article> searchArticles(@RequestParam("keyword") String keyword) {
		return articleService.searchByContent(keyword);
	}

}