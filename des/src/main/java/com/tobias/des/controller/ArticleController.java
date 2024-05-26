package com.tobias.des.controller;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
import org.springframework.web.multipart.MultipartFile;

import com.tobias.des.dto.requests.ArticleCreateRequest;
import com.tobias.des.dto.requests.ArticleUpdateRequest;
import com.tobias.des.dto.responses.ArticleResponse;
import com.tobias.des.entity.Article;
import com.tobias.des.repository.ArticlePhotosRepository;
import com.tobias.des.repository.ArticleRepository;
import com.tobias.des.service.ArticleService;
import com.tobias.des.service.UserDetailsServiceImpl;
import com.tobias.des.service.UserManager;

@RestController
@RequestMapping("/api/article")
public class ArticleController {
	private ArticlePhotosRepository articlePhotosRepository;
	private ArticleService articleService;
	private final UserManager userManager;
	private final UserDetailsServiceImpl userDetailsService;
	private ArticleRepository articleRepository;

	public ArticleController(ArticleService articleService, UserManager userManager,
			UserDetailsServiceImpl userDetailsService, ArticleRepository articleRepository,
			ArticlePhotosRepository articlePhotosRepository) {
		super();
		this.articleService = articleService;
		this.userManager = userManager;
		this.userDetailsService = userDetailsService;
		this.articleRepository = articleRepository;
		this.articlePhotosRepository = articlePhotosRepository;
	}

	@GetMapping("/search")
	public List<Article> searchArticles(@RequestParam("keyword") String keyword) {
		return articleService.searchByContent(keyword);
	}

	@GetMapping
	public List<ArticleResponse> getAllArticles() {
		return articleService.getAllArticles();
	}

	@GetMapping("/myarticles/{userId}")
	public List<Article> getAllArticlesByUserId(@PathVariable Long userId) {
		return articleService.getAllArticlesByUserId(userId);
	}

	@GetMapping("/my/{userId}")
	public List<ArticleResponse> getArticlesByUserId(@PathVariable Long userId) {
		return articleService.getArticlesByUserId(userId);
	}

	@PostMapping
	public ArticleResponse createOneArticle(@RequestBody ArticleCreateRequest newArticleCreateRequest) {
		return articleService.createOneArticle(newArticleCreateRequest);
	}

	@GetMapping("/{articleId}")
	public ResponseEntity<Article> getOneArticleById(@PathVariable Long articleId) {
		Article article = articleService.getOneArticleById(articleId);
		if (article != null) {
			// Post nesnesinin oluşturulma zamanını formatlayarak güncelleyin
			article.setCreatedAtFormatted(article.getFormattedCreatedAt());
			return ResponseEntity.ok().body(article);
		} else {
			// Post bulunamazsa uygun bir hata mesajı döndür
			return ResponseEntity.notFound().build();
		}
	}

	@PutMapping("/{articleId}")
	public Article updateOneArticleById(@PathVariable Long articleId, @RequestBody ArticleUpdateRequest updateArticle) {
		return articleService.updateOneArticleById(articleId, updateArticle);
	}

	@DeleteMapping("/{articleId}")
	public ResponseEntity<String> deleteOneArticleById(@PathVariable Long articleId) throws IOException {
		// Fotoğraf kayıtlarını sil
		articleService.deleteArticlePhotosByArticleId(articleId);

		// Makaleyi sil
		articleService.deleteOneArticleById(articleId);

		String articleFolder = "C:/campspring/des/src/main/resources/uploads/articles/" + articleId;
		File folder = new File(articleFolder);
		if (folder.exists()) {
			deleteFolder(folder);
		}

		return ResponseEntity.ok("Article and related photos deleted successfully.");
	}

	private void deleteFolder(File folder) {
		File[] files = folder.listFiles();
		if (files != null) {
			for (File file : files) {
				if (file.isDirectory()) {
					deleteFolder(file);
				} else {
					file.delete();
				}
			}
		}
		folder.delete();

	}

	@PutMapping("/photos/{articleId}")
	public ResponseEntity<List<String>> uploadArticlePhotos(@PathVariable Long articleId,
			@RequestParam("files") List<MultipartFile> files) {
		try {
			List<String> photoNames = articleService.uploadArticlePhotos(articleId, files);
			return ResponseEntity.ok().body(photoNames);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/photos/{articleId}/{photoId}")
	public ResponseEntity<Resource> getArticlePhoto(@PathVariable Long articleId, @PathVariable String photoId) {
		try {
			Resource photo = articleService.getArticlePhoto(articleId, photoId);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photo.getFilename() + "\"")
					.body(photo);
		} catch (IOException e) {
			// Hata durumunda varsayılan bir fotoğraf döndür

			return null;
		}
	}

	@GetMapping("/photos/{articleId}")
	public ResponseEntity<List<String>> getAllArticlePhotos(@PathVariable Long articleId) {
		List<String> photoNames = articleService.getAllArticlePhotos(articleId);
		return ResponseEntity.ok().body(photoNames);
	}

}
