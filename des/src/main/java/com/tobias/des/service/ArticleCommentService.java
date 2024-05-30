package com.tobias.des.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tobias.des.dto.requests.ArticleCommentCreateRequest;
import com.tobias.des.dto.requests.ArticleCommentUpdateRequest;
import com.tobias.des.dto.responses.ArticleCommentResponse;
import com.tobias.des.entity.Article;
import com.tobias.des.entity.ArticleComment;
import com.tobias.des.entity.User;
import com.tobias.des.repository.ArticleCommentRepository;
import com.tobias.des.repository.ArticleRepository;

import jakarta.transaction.Transactional;

@Service
public class ArticleCommentService {

	private ArticleCommentRepository articleCommentRepository;
	private UserManager userManager;
	private ArticleService articleService;
	private ArticleRepository articleRepository;

	public ArticleCommentService(ArticleCommentRepository articleCommentRepository, UserManager userManager,
			@Lazy ArticleService articleService, ArticleRepository articleRepository) {
		super();
		this.articleCommentRepository = articleCommentRepository;
		this.userManager = userManager;
		this.articleService = articleService;
		this.articleRepository = articleRepository;
	}

	public List<ArticleComment> getAllCommentsWithParam(Optional<Long> userId, Optional<Long> articleId) {
		if (userId.isPresent() && articleId.isPresent()) {
			return articleCommentRepository.findByUserIdAndArticleId(userId.get(), articleId.get());
		} else if (userId.isPresent()) {
			return articleCommentRepository.findByUserId(userId.get());
		} else if (articleId.isPresent()) {
			return articleCommentRepository.findByArticleId(articleId.get());
		} else
			return articleCommentRepository.findAll();

	}

	public List<ArticleComment> getAllCommentsWithParam(Optional<Long> userId) {
		if (userId.isPresent()) {
			return articleCommentRepository.findByUserId(userId.get());
		} else if (userId.isPresent()) {
			return articleCommentRepository.findByUserId(userId.get());
		} else
			return articleCommentRepository.findAll();

	}

	public ArticleComment getOneCommentById(Long articleCommentId) {
		return articleCommentRepository.findById(articleCommentId).orElse(null);
	}

	public ArticleComment createOneComment(ArticleCommentCreateRequest request) {
		User user = userManager.getOneUserById(request.getUserId());
		Article article = articleService.getOneArticleById(request.getArticleId());
		if (user != null && article != null) {
			ArticleComment commentToSave = new ArticleComment();
			commentToSave.setId(request.getId());
			commentToSave.setArticle(article);
			commentToSave.setUser(user);
			commentToSave.setText(request.getText());
			return articleCommentRepository.save(commentToSave);
		} else
			return null;
	}

	public ArticleComment updateOneCommentById(Long articleCommentId, ArticleCommentUpdateRequest request) {
		Optional<ArticleComment> articleComment = articleCommentRepository.findById(articleCommentId);
		if (articleComment.isPresent()) {
			ArticleComment commentToUpdate = articleComment.get();
			commentToUpdate.setText(request.getText());
			return articleCommentRepository.save(commentToUpdate);

		} else
			return null;
	}

	@Transactional
	public void deleteArticleComment(Long userId, Long articleId, Long articleCommentId) throws Exception {
		Optional<ArticleComment> commentOptional = articleCommentRepository.findById(articleCommentId);
		if (commentOptional.isPresent()) {
			ArticleComment articleComment = commentOptional.get();
			if (articleComment.getUser().getId().equals(userId)
					|| articleComment.getArticle().getUser().getId().equals(userId)) {
				articleCommentRepository.delete(articleComment);
			} else {
				// Kullanıcı bu yorumu silme yetkisine sahip değil
				throw new Exception("Bu yorumu silme yetkiniz yok.");
			}
		} else {
			// Verilen commentId'ye sahip yorum bulunamadı
			throw new Exception("Verilen ID'ye sahip yorum bulunamadı.");
		}
	}

	public List<ArticleComment> getCommentsArticleId(Long articleId) {

		return articleCommentRepository.findByArticleId(articleId);
	}

	public List<ArticleCommentResponse> getCommentsResponseByArticleId(Long articleId) {
		List<ArticleComment> comments = articleCommentRepository.findByArticleId(articleId);
		return comments.stream().map(comment -> {
			comment.setFormattedCreatedAt(comment.getFormattedCreatedAt()); // Formatlı tarihi ayarla
			return new ArticleCommentResponse(comment);
		}).collect(Collectors.toList());
	}

}
