package com.tobias.des.controller;

import java.io.IOException;
import java.util.List;
import java.util.Optional;

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

	@GetMapping
	public List<PostResponse> getAllPosts(@RequestParam Optional<Long> userId) {
		return postService.getAllPosts(userId);
	}

	@PostMapping
	public PostResponse createOnePost(@RequestBody PostCreateRequest newPostCreateRequest) {
		return postService.createOnePost(newPostCreateRequest);
	}

	@GetMapping("/{postId}")
	public Post getOnePostById(@PathVariable Long postId) {
		return postService.getOnePostById(postId);
	}

	@GetMapping("/me/{userId}")
	public List<Post> getAllPostsByUserId(@PathVariable Long userId) {
		return postService.getAllPostsByUserId(userId);
	}

	@PutMapping("/{postId}")
	public Post updateOnePostById(@PathVariable Long postId, @RequestBody PostUpdateRequest updatePost) {
		return postService.updateOnePostById(postId, updatePost);
	}

	@DeleteMapping("/{postId}")
	public void deleteOnePostById(@PathVariable Long postId) {
		postService.deleteOnePostById(postId);
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

	@GetMapping("/photos/{postId}")
	public ResponseEntity<Resource> getPostPhoto(@PathVariable Long postId) {
		try {
			Resource photo = postService.getPostPhoto(postId);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photo.getFilename() + "\"")
					.body(photo);
		} catch (IOException e) {
			return ResponseEntity.notFound().build();
		}
	}
}
