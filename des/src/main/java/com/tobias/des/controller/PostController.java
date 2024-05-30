package com.tobias.des.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

import org.apache.tomcat.util.http.fileupload.FileUtils;
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

import com.tobias.des.dto.Constants;
import com.tobias.des.dto.requests.PostCreateRequest;
import com.tobias.des.dto.requests.PostUpdateRequest;
import com.tobias.des.dto.responses.PostResponse;
import com.tobias.des.entity.Post;
import com.tobias.des.repository.PostRepository;
import com.tobias.des.service.PostService;
import com.tobias.des.service.UserDetailsServiceImpl;
import com.tobias.des.service.UserManager;

@RestController
@RequestMapping("/api/post")
public class PostController {

	private PostService postService;
	private final UserManager userManager;
	private final UserDetailsServiceImpl userDetailsService;
	private PostRepository postRepository;

	public PostController(PostService postService, UserManager userManager, UserDetailsServiceImpl userDetailsService,
			PostRepository postRepository) {
		super();
		this.postService = postService;
		this.userManager = userManager;
		this.userDetailsService = userDetailsService;
		this.postRepository = postRepository;

	}

	@GetMapping("/search")
	public List<Post> searchPosts(@RequestParam("keyword") String keyword) {
		return postService.searchByContent(keyword);
	}

	@GetMapping
	public List<PostResponse> getAllPosts() {
		return postService.getAllPosts();
	}

	@GetMapping("/{postId}")
	public ResponseEntity<Post> getOnePostsById(@PathVariable Long postId) {
		return postService.getOnePostsById(postId);
	}

	@GetMapping("/my/{userId}")
	public List<PostResponse> getPostsByUserId(@PathVariable Long userId) {
		return postService.getPostsByUserId(userId);
	}

	@PostMapping
	public PostResponse createOnePost(@RequestBody PostCreateRequest newPostCreateRequest) {
		return postService.createOnePost(newPostCreateRequest);
	}

	@PutMapping("/{postId}")
	public Post updateOnePostById(@PathVariable Long postId, @RequestBody PostUpdateRequest updatePost) {
		return postService.updateOnePostById(postId, updatePost);
	}

	@DeleteMapping("/{postId}")
	public void deleteOnePostById(@PathVariable Long postId) {
		try {
			// Postu sil
			postService.deleteOnePostById(postId);

			// Fotoğraf klasörünü kontrol et ve varsa içeriğini sil
			String photoFolderPath = Constants.POST_PHOTOS_DIR + postId;
			Path photoFolder = Paths.get(photoFolderPath);
			if (Files.exists(photoFolder)) {
				FileUtils.deleteDirectory(photoFolder.toFile());
			}

			// Fotoğraf isimlerini post_photos tablosundan sil
			postService.deletePostPhoto(postId);

			String articleFolder = "C:/campspring/des/src/main/resources/uploads/posts/" + postId;
			File folder = new File(articleFolder);
			if (folder.exists()) {
				deleteFolder(folder);
			}

		} catch (IOException e) {
			e.printStackTrace();
			// Hata oluşursa uygun bir şekilde işleyin
		}
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

	@PutMapping("/photos/{postId}")
	public ResponseEntity<List<String>> uploadPostPhotos(@PathVariable Long postId,
			@RequestParam("files") List<MultipartFile> files) {
		try {
			List<String> photoNames = postService.uploadPostPhotos(postId, files);
			return ResponseEntity.ok().body(photoNames);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
		}
	}

	@GetMapping("/photos/{postId}/{photoId}")
	public ResponseEntity<Resource> getPostPhoto(@PathVariable Long postId, @PathVariable String photoId) {
		try {
			Resource photo = postService.getPostPhoto(postId, photoId);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photo.getFilename() + "\"")
					.body(photo);
		} catch (IOException e) {
			// Hata durumunda varsayılan bir fotoğraf döndür

			return null;
		}
	}

	@GetMapping("/photos/{postId}")
	public ResponseEntity<List<String>> getAllPostPhotos(@PathVariable Long postId) {
		List<String> photoNames = postService.getAllPostPhotos(postId);
		return ResponseEntity.ok().body(photoNames);
	}

}
