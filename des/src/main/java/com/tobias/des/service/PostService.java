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
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tobias.des.dto.Constants;
import com.tobias.des.dto.requests.PostCreateRequest;
import com.tobias.des.dto.requests.PostUpdateRequest;
import com.tobias.des.dto.responses.LikeResponse;
import com.tobias.des.dto.responses.PostResponse;
import com.tobias.des.entity.Article;
import com.tobias.des.entity.Post;
import com.tobias.des.entity.PostPhotos;
import com.tobias.des.entity.PostVideos;
import com.tobias.des.repository.ArticleRepository;
import com.tobias.des.repository.PostPhotosRepository;
import com.tobias.des.repository.PostRepository;
import com.tobias.des.repository.PostVideosRepository;
import com.tobias.des.repository.UserRepository;

@Service
public class PostService {

	private PostRepository postRepository;
	private PostPhotosRepository postPhotosRepository;
	private PostVideosRepository postVideosRepository;
	private UserManager authManager;
	private LikeService likeService;
	private ArticleRepository articleRepository;
	private UserRepository userRepository;

	public PostService(PostRepository postRepository, ArticleRepository articleRepository,
			UserRepository userRepository, UserManager authManager, PostVideosRepository postVideosRepository,
			PostPhotosRepository postPhotosRepository) {
		super();
		this.postRepository = postRepository;
		this.authManager = authManager;
		this.articleRepository = articleRepository;
		this.userRepository = userRepository;
		this.postPhotosRepository = postPhotosRepository;
		this.postVideosRepository = postVideosRepository;
	}

	public List<PostResponse> getPosts(int limit, int offset) {
		Pageable pageable = PageRequest.of(offset / limit, limit, Sort.by(Sort.Direction.DESC, "createdAt"));
		Page<Post> page = postRepository.findAll(pageable);
		return page.stream().map(this::convertToPostResponse).collect(Collectors.toList());
	}

	private PostResponse convertToPostResponse(Post post) {
		PostResponse postResponse = new PostResponse();
		postResponse.setId(post.getId());
		postResponse.setTitle(post.getTitle());
		postResponse.setText(post.getText());
		postResponse.setUserId(post.getUser().getId());
		postResponse.setUserName(post.getUser().getUsername());
		postResponse.setConnections(post.getConnections());
		postResponse.setCreatedAt(post.getCreatedAt());
		postResponse.setFormattedCreatedAt(post.getFormattedCreatedAt());
		return postResponse;
	}

	public List<Long> getAllPostIds() {
		return postRepository.findAll().stream().map(Post::getId).collect(Collectors.toList());
	}

	public List<Post> searchByContent(String keyword) {
		return postRepository.findByContentContaining(keyword);
	}

	@Autowired
	public void setLikeService(LikeService likeService) {
		this.likeService = likeService;
	}

	public List<PostResponse> getAllPosts() {
		List<Post> list = postRepository.findAll(Sort.by(Sort.Direction.DESC, "createdAt"));
		return list.stream().map(p -> {
			List<LikeResponse> likes = likeService.getAllLikesWithParam(Optional.ofNullable(null),
					Optional.of(p.getId()));
			return new PostResponse(p, likes);
		}).collect(Collectors.toList());
	}

	public Post getOnePostById(Long postId) {
		return postRepository.findById(postId).orElse(null);
	}

	public PostResponse getOnePostByPostId(Long postId) {
		Post post = postRepository.findById(postId).orElse(null);
		if (post != null) {
			post.setCreatedAtFormatted(post.getFormattedCreatedAt());
			List<LikeResponse> likes = likeService.getAllLikesWithParam(Optional.ofNullable(null), Optional.of(postId));
			return new PostResponse(post, likes);
		} else {
			return null;
		}
	}

	public List<PostResponse> getPostsByUserId(Long userId) {
		List<Post> userPosts = postRepository.findByUserId(userId);
		return userPosts.stream().map(post -> {
			List<LikeResponse> postLikes = likeService.getAllLikesWithParam(Optional.ofNullable(null),
					Optional.of(post.getId()));
			return new PostResponse(post, postLikes);
		}).collect(Collectors.toList());
	}

	public Post createOnePost(PostCreateRequest newPostCreateRequest) {
		Post newPost = new Post();
		newPost.setTitle(newPostCreateRequest.getTitle());
		newPost.setText(newPostCreateRequest.getText());
		newPost.setConnections(newPostCreateRequest.getConnections());
		newPost.setUser(userRepository.findById(newPostCreateRequest.getUserId()).orElse(null)); // Kullanıcıyı
																									// ilişkilendirme

		if (newPostCreateRequest.getArticleId() != null) {
			Article article = articleRepository.findById(newPostCreateRequest.getArticleId()).orElse(null);
			if (article != null) {
				newPost.setArticle(article);
			} else {
				return null; // İlişkili makale bulunamadıysa null döndür
			}
		}

		return postRepository.save(newPost); // Post nesnesini kaydet ve döndür
	}

	/*
	 * public Post updateOnePostById(Long postId, PostUpdateRequest updatePost) {
	 * Optional<Post> post = postRepository.findById(postId); if (post.isPresent())
	 * { Post toUpdate = post.get(); toUpdate.setText(updatePost.getText());
	 * toUpdate.setTitle(updatePost.getTitle());
	 * toUpdate.setTitle(updatePost.getTitle()); postRepository.save(toUpdate);
	 * return toUpdate; } return null; }
	 */
	public Post updateOnePostById(Long postId, PostUpdateRequest updatePost) {
		Optional<Post> post = postRepository.findById(postId);
		if (post.isPresent()) {
			Post toUpdate = post.get();
			toUpdate.setText(updatePost.getText());
			toUpdate.setTitle(updatePost.getTitle());
			toUpdate.setConnections(updatePost.getConnections());
			if (updatePost.getArticleId() != null) {
				Article article = articleRepository.findById(updatePost.getArticleId()).orElse(null);
				if (article != null) {
					toUpdate.setArticle(article);
				} else {
					return null; // If related article is not found, return null
				}
			} else {
				toUpdate.setArticle(null); // If no article ID is provided, unset the article
			}

			postRepository.save(toUpdate);
			return toUpdate;
		}
		return null;
	}

	public void deleteOnePostById(Long postId) {
		// Posta bağlı tüm fotoğrafları sil
		List<PostPhotos> postPhotos = postPhotosRepository.findByPostId(postId);
		for (PostPhotos postPhoto : postPhotos) {
			postPhotosRepository.delete(postPhoto);
		}

		// Posta bağlı tüm videoları sil
		List<PostVideos> postVideos = postVideosRepository.findByPostId(postId);
		for (PostVideos postVideo : postVideos) {
			postVideosRepository.delete(postVideo);
		}

		// Postu sil
		postRepository.deleteById(postId);
	}

	public void deletePostPhoto(Long postId) throws IOException {
		Path filePath = Paths.get(Constants.POST_PHOTOS_DIR + postId + ".jpeg");
		if (Files.exists(filePath)) {
			Files.delete(filePath);
		} else {
			throw new IOException("Post photo not found for post id: " + postId);
		}
	}

	public Post getOnePostByUserId(Long userId) {
		// Kullanıcıya ait bir postu getirmek için findByUserId metodu kullanılmalıdır.
		List<Post> userPosts = postRepository.findByUserId(userId);
		// Eğer kullanıcının hiç postu yoksa veya birden fazla postu varsa uygun bir
		// işlem yapılmalıdır.
		// Burada sadece ilk postu döndürüyorum, ancak uygulama mantığınıza göre işlemi
		// ayarlamanız gerekebilir.
		if (!userPosts.isEmpty()) {
			return userPosts.get(0);
		} else {
			return null; // veya uygun bir hata mesajı döndürebilirsiniz.
		}
	}

	public List<String> uploadPostPhotos(Long postId, List<MultipartFile> files) throws Exception {
		List<String> photoNames = new ArrayList<>();

		// Postun fotoğraflarını saklamak için bir klasör oluştur
		String postPhotoDirPath = Constants.POST_PHOTOS_DIR + postId + "/";
		Files.createDirectories(Paths.get(postPhotoDirPath));

		// Fotoğrafları kaydet
		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			if (file.isEmpty()) {
				throw new Exception("Dosya boş olamaz");
			}

			try {
				// Dosya adını oluştur
				String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
				String fileName = postId + "_" + (i + 1) + "." + extension;
				String filePath = postPhotoDirPath + fileName;

				// Dosyayı kaydet
				byte[] bytes = file.getBytes();
				Path path = Paths.get(filePath);
				Files.write(path, bytes);

				photoNames.add(fileName);

				// PostPhotos entity'sine kayıt ekle
				PostPhotos postPhoto = new PostPhotos();
				postPhoto.setPhotoName(fileName);
				postPhoto.setPost(postRepository.getById(postId)); // postRepository'e ihtiyaç duyulacak
				// PostPhotos entity'sini kaydet
				postPhotosRepository.save(postPhoto);
			} catch (Exception e) {
				throw new Exception("Dosya yüklenirken bir hata oluştu", e);
			}
		}

		return photoNames;
	}

	private String generateFileName(Long postId, String extension) {
		return postId + "." + extension;
	}

	private static final String UPLOAD_DIR = "/des/des/src/main/resources/uploads/posts/";

	public Resource getPostPhoto(Long postId, String photoId) throws IOException {
		String photoPath = Constants.POST_PHOTOS_DIR + postId + "/" + photoId;
		Path filePath = Paths.get(photoPath);
		Resource resource = new PathResource(filePath);
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return resource;
		} else {
			throw new IOException("Post photo not found for post id: " + postId + " and photo id: " + photoId);
		}
	}

	public List<String> getAllPostPhotos(Long postId) {
		List<String> photoNames = new ArrayList<>();
		List<PostPhotos> postPhotos = postPhotosRepository.findByPostId(postId);
		for (PostPhotos postPhoto : postPhotos) {
			photoNames.add(postPhoto.getPhotoName());
		}
		return photoNames;
	}

	public List<String> uploadPostVideos(Long postId, List<MultipartFile> files) throws Exception {
		List<String> videoNames = new ArrayList<>();
		String postVideoDirPath = Constants.VIDEO_UPLOAD_DIR + postId + "/";
		Files.createDirectories(Paths.get(postVideoDirPath));

		for (int i = 0; i < files.size(); i++) {
			MultipartFile file = files.get(i);
			if (file.isEmpty()) {
				throw new Exception("Dosya boş olamaz");
			}

			try {
				String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
				String fileName = postId + "_" + (i + 1) + "." + extension;
				String filePath = postVideoDirPath + fileName;

				byte[] bytes = file.getBytes();
				Path path = Paths.get(filePath);
				Files.write(path, bytes);

				videoNames.add(fileName);

				PostVideos postVideo = new PostVideos();
				postVideo.setVideoName(fileName);
				postVideo.setPost(postRepository.getById(postId));
				postVideosRepository.save(postVideo);
			} catch (Exception e) {
				throw new Exception("Dosya yüklenirken bir hata oluştu", e);
			}
		}

		return videoNames;
	}

	public Resource getPostVideo(Long postId, String videoId) throws IOException {
		String videoPath = Constants.VIDEO_UPLOAD_DIR + postId + "/" + videoId;
		Path filePath = Paths.get(videoPath);
		Resource resource = new PathResource(filePath);
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return resource;
		} else {
			throw new IOException("Post video not found for post id: " + postId + " and video id: " + videoId);
		}
	}

	public List<String> getAllPostVideos(Long postId) {
		List<String> videoNames = new ArrayList<>();
		List<PostVideos> postVideos = postVideosRepository.findByPostId(postId);
		for (PostVideos postVideo : postVideos) {
			videoNames.add(postVideo.getVideoName());
		}
		return videoNames;
	}
}