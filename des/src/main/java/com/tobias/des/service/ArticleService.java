package com.tobias.des.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tobias.des.dto.Constants;
import com.tobias.des.dto.requests.ArticleCreateRequest;
import com.tobias.des.dto.requests.ArticleUpdateRequest;
import com.tobias.des.dto.responses.ArticleLikeResponse;
import com.tobias.des.dto.responses.ArticleResponse;
import com.tobias.des.entity.Article;
import com.tobias.des.entity.ArticlePhotos;
import com.tobias.des.entity.User;
import com.tobias.des.repository.ArticlePhotosRepository;
import com.tobias.des.repository.ArticleRepository;

@Service
public class ArticleService {

	private ArticleRepository articleRepository;
	private ArticlePhotosRepository articlePhotosRepository;
	private UserManager authManager;
	private ArticleLikeService articleLikeService;

	public ArticleService(ArticleRepository articleRepository, UserManager authManager,
			ArticlePhotosRepository articlePhotosRepository, @Lazy ArticleLikeService articleLikeService) {
		super();
		this.articleRepository = articleRepository;
		this.authManager = authManager;
		this.articlePhotosRepository = articlePhotosRepository;
		this.articleLikeService = articleLikeService;

	}

	public List<Article> searchByContent(String keyword) {
		return articleRepository.findByContentContaining(keyword);
	}

	@Autowired
	public void setLikeService(ArticleLikeService likeService) {
		this.articleLikeService = articleLikeService;
	}

	public List<ArticleResponse> getAllArticles() {
		List<Article> list = articleRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
		return list.stream().map(a -> {
			List<ArticleLikeResponse> likes = articleLikeService.getAllArticleLikesWithParam(Optional.ofNullable(null),
					Optional.of(a.getId()));
			return new ArticleResponse(a, likes);
		}).collect(Collectors.toList());
	}

	public List<ArticleResponse> getArticlesByUserId(Long userId) {
		List<Article> userArticles = articleRepository.findByUserId(userId);
		List<ArticleResponse> articleResponses = new ArrayList<>();
		for (Article article : userArticles) {
			List<ArticleLikeResponse> articleLikes = articleLikeService
					.getAllArticleLikesWithParam(Optional.ofNullable(null), Optional.of(article.getId()));
			articleResponses.add(new ArticleResponse(article, articleLikes));
		}
		return articleResponses;
	}

	public Article getOneArticleById(Long articleId) {
		return articleRepository.findById(articleId).orElse(null);
	}

	public ArticleResponse createOneArticle(ArticleCreateRequest newArticleCreateRequest) {
		User user = authManager.getOneUserById(newArticleCreateRequest.getUserId());
		if (user == null)
			return null;
		Article toSave = new Article();
		toSave.setSubject(newArticleCreateRequest.getSubject());
		toSave.setContent(newArticleCreateRequest.getContent());
		toSave.setUser(user);
		Article savedArticle = articleRepository.save(toSave);

		// PostResponse oluştur ve döndür
		List<ArticleLikeResponse> likes = articleLikeService.getAllArticleLikesWithParam(Optional.ofNullable(null),
				Optional.of(savedArticle.getId()));
		return new ArticleResponse(savedArticle, likes);
	}

	public Article updateOneArticleById(Long articleId, ArticleUpdateRequest updateArticle) {
		Optional<Article> article = articleRepository.findById(articleId);
		if (article.isPresent()) {
			Article toUpdate = article.get();
			toUpdate.setSubject(updateArticle.getSubject());
			toUpdate.setContent(updateArticle.getContent());
			articleRepository.save(toUpdate);
			return toUpdate;
		}
		return null;
	}

	public void deleteOneArticleById(Long articleId) {
		articleRepository.deleteById(articleId);
	}

	public void deleteArticlePhotosByArticleId(Long articleId) {
		List<ArticlePhotos> articlePhotos = articlePhotosRepository.findByArticleId(articleId);
		for (ArticlePhotos photo : articlePhotos) {
			articlePhotosRepository.delete(photo);
		}
	}

	public Article getOneArticleByUserId(Long userId) {
		// Kullanıcıya ait bir postu getirmek için findByUserId metodu kullanılmalıdır.
		List<Article> userArticles = articleRepository.findByUserId(userId);

		if (!userArticles.isEmpty()) {
			return userArticles.get(0);
		} else {
			return null; // veya uygun bir hata mesajı döndürebilirsiniz.
		}
	}

	public List<String> uploadArticlePhotos(Long articleId, List<MultipartFile> files) throws Exception {
		List<String> photoNames = new ArrayList<>();

		// Postun fotoğraflarını saklamak için bir klasör oluştur
		String articlePhotoDirPath = Constants.ARTICLE_PHOTOS_DIR + articleId + "/";
		Files.createDirectories(Paths.get(articlePhotoDirPath));

		// Fotoğrafları kaydet
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			if (file.isEmpty()) {
				throw new Exception("Dosya boş olamaz");
			}

			try {
				// Dosya adını oluştur
				String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
				String fileName = articleId + "_" + (i + 1) + "." + extension;
				String filePath = articlePhotoDirPath + fileName;

				// Dosyayı kaydet
				byte[] bytes = file.getBytes();
				Path path = Paths.get(filePath);
				Files.write(path, bytes);

				photoNames.add(fileName);

				// PostPhotos entity'sine kayıt ekle
				ArticlePhotos articlePhoto = new ArticlePhotos();
				articlePhoto.setPhotoName(fileName);
				articlePhoto.setArticle(articleRepository.getById(articleId)); // postRepository'e ihtiyaç duyulacak
				// PostPhotos entity'sini kaydet
				articlePhotosRepository.save(articlePhoto);
			} catch (Exception e) {
				throw new Exception("Dosya yüklenirken bir hata oluştu", e);
			}
		}

		return photoNames;
	}

	private String generateFileName(Long articleId, String extension) {
		return articleId + "." + extension;
	}

	private static final String UPLOAD_DIR = "C:/campspring/des/src/main/resources/uploads/articles/";

	public Resource getArticlePhoto(Long articleId, String photoId) throws IOException {
		String photoPath = Constants.ARTICLE_PHOTOS_DIR + articleId + "/" + photoId;
		Path filePath = Paths.get(photoPath);
		Resource resource = new PathResource(filePath);
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return resource;
		} else {
			throw new IOException("Article photo not found for article id: " + articleId + " and photo id: " + photoId);
		}
	}

	public List<String> getAllArticlePhotos(Long articleId) {
		List<String> photoNames = new ArrayList<>();
		List<ArticlePhotos> articlePhotos = articlePhotosRepository.findByArticleId(articleId);
		for (ArticlePhotos articlePhoto : articlePhotos) {
			photoNames.add(articlePhoto.getPhotoName());
		}
		return photoNames;
	}
}
