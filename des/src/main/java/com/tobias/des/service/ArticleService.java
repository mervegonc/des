package com.tobias.des.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.tobias.des.dto.requests.ArticleUpdateRequest;
import com.tobias.des.dto.responses.ArticleResponse;
import com.tobias.des.entity.Article;
import com.tobias.des.entity.User;
import com.tobias.des.exception.ResourceNotFoundException;
import com.tobias.des.repository.ArticleRepository;
import com.tobias.des.repository.UserRepository;

@Service
public class ArticleService {

	private final ArticleRepository articleRepository;
	private final UserRepository userRepository;

	@Autowired
	public ArticleService(ArticleRepository articleRepository, UserRepository userRepository) {
		this.articleRepository = articleRepository;
		this.userRepository = userRepository;
	}

	public Article createArticle(Long userId, String subject, String content) {
		// Kullanıcı var mı kontrol et
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

		// Yeni makale oluştur
		Article article = new Article();
		article.setUser(user);
		article.setSubject(subject);
		article.setContent(content);

		// Makaleyi kaydet ve geri döndür
		return articleRepository.save(article);
	}

	public List<ArticleResponse> getAllArticles() {
		List<Article> articles = articleRepository.findAll();
		List<ArticleResponse> articleResponses = articles.stream().map(ArticleResponse::new)
				.collect(Collectors.toList());
		return articleResponses;
	}

	public List<ArticleResponse> getAllArticlesByUserId(Long userId) {
		// Kullanıcı var mı kontrol et
		User user = userRepository.findById(userId)
				.orElseThrow(() -> new ResourceNotFoundException("User", "id", userId));

		// Kullanıcıya ait tüm makaleleri al
		List<Article> articles = articleRepository.findByUser(user);
		List<ArticleResponse> articleResponses = articles.stream().map(ArticleResponse::new)
				.collect(Collectors.toList());
		return articleResponses;
	}

	public Article updateOneArticleById(Long articleId, ArticleUpdateRequest updateArticle) {
		Article article = articleRepository.findById(articleId)
				.orElseThrow(() -> new ResourceNotFoundException("Article", "id", articleId));

		// Güncelleme yap
		article.setSubject(updateArticle.getSubject());
		article.setContent(updateArticle.getContent());

		// Güncellenmiş makaleyi kaydet ve geri döndür
		return articleRepository.save(article);
	}

	public void deleteOneArticleById(Long articleId) {
		articleRepository.deleteById(articleId);
	}

	public List<Article> searchByContent(String keyword) {
		return articleRepository.findByContentContaining(keyword);
	}

}
