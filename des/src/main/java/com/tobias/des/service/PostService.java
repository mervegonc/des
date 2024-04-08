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
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.tobias.des.dto.Constants;
import com.tobias.des.dto.requests.PostCreateRequest;
import com.tobias.des.dto.requests.PostUpdateRequest;
import com.tobias.des.dto.responses.LikeResponse;
import com.tobias.des.dto.responses.PostResponse;
import com.tobias.des.entity.Post;
import com.tobias.des.entity.User;
import com.tobias.des.repository.PostRepository;

@Service
public class PostService {

	private PostRepository postRepository;
	private UserManager authManager;
	private LikeService likeService;

	public PostService(PostRepository postRepository, UserManager authManager) {
		super();
		this.postRepository = postRepository;
		this.authManager = authManager;

	}

	@Autowired
	public void setLikeService(LikeService likeService) {
		this.likeService = likeService;
	}

	public List<PostResponse> getAllPosts(Optional<Long> userId) {
		List<Post> list;
		if (userId.isPresent()) {
			list = postRepository.findByUserId(userId.get());
		}
		list = postRepository.findAll();
		return list.stream().map(p -> {
			List<LikeResponse> likes = likeService.getAllLikesWithParam(Optional.ofNullable(null),
					Optional.of(p.getId()));
			return new PostResponse(p, likes);
		}).collect(Collectors.toList());
	}

	public Post getOnePostById(Long postId) {
		return postRepository.findById(postId).orElse(null);
	}

	/*
	 * public Post createOnePost(PostCreateRequest newPostCreateRequest) { User user
	 * = authManager.getOneUserById(newPostCreateRequest.getUserId()); if (user ==
	 * null) return null; Post toSave = new Post();
	 * toSave.setId(newPostCreateRequest.getId());
	 * toSave.setText(newPostCreateRequest.getText());
	 * toSave.setTitle(newPostCreateRequest.getTitle()); toSave.setUser(user);
	 * return postRepository.save(toSave); }
	 */
	public PostResponse createOnePost(PostCreateRequest newPostCreateRequest) {
		User user = authManager.getOneUserById(newPostCreateRequest.getUserId());
		if (user == null)
			return null;
		Post toSave = new Post();
		toSave.setText(newPostCreateRequest.getText());
		toSave.setTitle(newPostCreateRequest.getTitle());
		toSave.setUser(user);
		Post savedPost = postRepository.save(toSave);

		// PostResponse oluştur ve döndür
		List<LikeResponse> likes = likeService.getAllLikesWithParam(Optional.ofNullable(null),
				Optional.of(savedPost.getId()));
		return new PostResponse(savedPost, likes);
	}

	public Post updateOnePostById(Long postId, PostUpdateRequest updatePost) {
		Optional<Post> post = postRepository.findById(postId);
		if (post.isPresent()) {
			Post toUpdate = post.get();
			toUpdate.setText(updatePost.getText());
			toUpdate.setTitle(updatePost.getTitle());
			postRepository.save(toUpdate);
			return toUpdate;
		}
		return null;
	}

	public void deleteOnePostById(Long postId) {
		postRepository.deleteById(postId);
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

	public List<Post> getAllPostsByUserId(Long userId) {
		return postRepository.findByUserId(userId);
	}

	/*
	 * public List<String> uploadPostPhotos(Long postId, List<MultipartFile> files)
	 * throws Exception { List<String> photoNames = new ArrayList<>();
	 * 
	 * for (MultipartFile file : files) { if (file.isEmpty()) { throw new
	 * Exception("Dosya boş olamaz"); }
	 * 
	 * try { // Dosya adını oluştur String extension =
	 * StringUtils.getFilenameExtension(file.getOriginalFilename()); String fileName
	 * = postId + "_" + UUID.randomUUID().toString() + "." + extension; String
	 * filePath = Constants.POST_PHOTOS_DIR + fileName;
	 * 
	 * // Dosyayı kaydet byte[] bytes = file.getBytes(); Path path =
	 * Paths.get(filePath); Files.write(path, bytes);
	 * 
	 * photoNames.add(fileName); } catch (Exception e) { throw new
	 * Exception("Dosya yüklenirken bir hata oluştu", e); } }
	 * 
	 * return photoNames; }
	 * 
	 */
	/*
	 * public List<String> uploadPostPhotos(Long postId, List<MultipartFile> files)
	 * throws Exception { List<String> photoNames = new ArrayList<>();
	 * 
	 * for (MultipartFile file : files) { if (file.isEmpty()) { throw new
	 * Exception("Dosya boş olamaz"); }
	 * 
	 * try { // Dosya adını oluştur String extension =
	 * StringUtils.getFilenameExtension(file.getOriginalFilename()); String fileName
	 * = postId + "_" + UUID.randomUUID().toString() + "." + extension; String
	 * filePath = Constants.POST_PHOTOS_DIR + fileName;
	 * 
	 * // Dosyayı kaydet byte[] bytes = file.getBytes(); Path path =
	 * Paths.get(filePath); Files.write(path, bytes);
	 * 
	 * photoNames.add(fileName); } catch (Exception e) { throw new
	 * Exception("Dosya yüklenirken bir hata oluştu", e); } }
	 * 
	 * return photoNames; }
	 */
	public List<String> uploadPostPhotos(Long postId, List<MultipartFile> files) throws Exception {
		List<String> photoNames = new ArrayList<>();

		for (MultipartFile file : files) {
			if (file.isEmpty()) {
				throw new Exception("Dosya boş olamaz");
			}

			try {
				// Dosya adını oluştur
				String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
				String fileName = generateFileName(postId, extension);
				String filePath = Constants.POST_PHOTOS_DIR + fileName;

				// Dosyayı kaydet
				byte[] bytes = file.getBytes();
				Path path = Paths.get(filePath);
				Files.write(path, bytes);

				photoNames.add(fileName);
			} catch (Exception e) {
				throw new Exception("Dosya yüklenirken bir hata oluştu", e);
			}
		}

		return photoNames;
	}

	private String generateFileName(Long postId, String extension) {
		return postId + "." + extension;
	}

	private static final String UPLOAD_DIR = "C:/campspring/des/src/main/java/com/tobias/des/uploads/posts/";

	public Resource getPostPhoto(Long postId) throws IOException {
		Path filePath = Paths.get(UPLOAD_DIR + postId + ".jpeg");
		Resource resource = new PathResource(filePath);
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return resource;
		} else {
			throw new IOException("Post photo not found for post id: " + postId);
		}
	}
}
