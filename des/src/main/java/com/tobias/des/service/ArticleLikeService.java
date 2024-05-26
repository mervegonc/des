package com.tobias.des.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tobias.des.dto.requests.ArticleLikeCreateRequest;
import com.tobias.des.dto.responses.ArticleLikeResponse;
import com.tobias.des.entity.Article;
import com.tobias.des.entity.ArticleLike;
import com.tobias.des.entity.User;
import com.tobias.des.repository.ArticleLikeRepository;

import jakarta.transaction.Transactional;

@Service
@Lazy
public class ArticleLikeService {

	private ArticleLikeRepository articleLikeRepository;
	private UserManager authManager;
	private ArticleService articleService;

	public ArticleLikeService(ArticleLikeRepository articleLikeRepository, UserManager authManager,
			ArticleService articleService) {
		super();
		this.articleLikeRepository = articleLikeRepository;
		this.authManager = authManager;
		this.articleService = articleService;
	}

	public ArticleLike createOneLike(ArticleLikeCreateRequest request) {
		// Kullanıcı ve post bilgilerini al
		User user = authManager.getOneUserById(request.getUserId());
		Article article = articleService.getOneArticleById(request.getArticleId());

		// Kullanıcının daha önce bu postu beğenip beğenmediğini kontrol et
		boolean alreadyLiked = articleLikeRepository.existsByUserIdAndArticleId(user.getId(), article.getId());

		// Eğer kullanıcı daha önce bu postu beğenmişse işlemi gerçekleştirme
		if (alreadyLiked) {
			return null;
		} else {
			// Yeni bir Like oluştur ve kaydet
			ArticleLike likeToSave = new ArticleLike();
			likeToSave.setId(request.getId());
			likeToSave.setArticle(article);
			likeToSave.setUser(user); // Burada like'ı atan kullanıcının ID'sini ekleyin
			return articleLikeRepository.save(likeToSave);
		}
	}

	public boolean hasUserLikedArticle(Long userId, Long articleId) {
		return articleLikeRepository.existsByUserIdAndArticleId(userId, articleId);
	}

	public ArticleLike getOneArticleLikeById(Long articleLikeId) {
		return articleLikeRepository.findById(articleLikeId).orElse(null);
	}

	/*
	 * public void deleteOneLikeById(Long userId, Long postId) {
	 * likeRepository.deleteById(userId, postId);
	 * 
	 * }
	 */
	@Transactional
	public void deleteOneArticleLikeById(Long userId, Long articleId) {
		articleLikeRepository.deleteById(userId, articleId);
	}

	public List<ArticleLikeResponse> getAllArticleLikesWithParam(Optional<Long> userId, Optional<Long> articleId) {
		List<ArticleLike> list;
		if (userId.isPresent() && articleId.isPresent()) {
			list = articleLikeRepository.findByUserIdAndArticleId(userId.get(), articleId.get());
		} else if (userId.isPresent()) {
			list = articleLikeRepository.findByUserId(userId.get());
		} else if (articleId.isPresent()) {
			list = articleLikeRepository.findByArticleId(articleId.get());
		} else
			list = articleLikeRepository.findAll();
		return list.stream().map(articleLike -> new ArticleLikeResponse(articleLike)).collect(Collectors.toList());
	}

	public Long getTotalLikesForArticleId(Long articleId) {
		List<ArticleLike> likesForArticle = articleLikeRepository.findByArticleId(articleId);
		return (long) likesForArticle.size();
	}

	public Long getLikeIdByUserIdAndArticleId(Long userId, Long articleId) {
		return articleLikeRepository.findIdByUserIdAndArticleId(userId, articleId);
	}

	public List<ArticleLikeResponse> getAllLikesForArticleId(Optional<Long> articleId) {
		if (articleId.isPresent()) {
			List<ArticleLike> likesForArticle = articleLikeRepository.findByArticleId(articleId.get());
			return likesForArticle.stream().map(articleLike -> new ArticleLikeResponse(articleLike))
					.collect(Collectors.toList());
		} else {
			// postId belirtilmemişse veya bulunamamışsa boş bir liste döndür
			return Collections.emptyList();
		}
	}

}
