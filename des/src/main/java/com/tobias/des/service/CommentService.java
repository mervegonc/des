package com.tobias.des.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tobias.des.dto.requests.CommentCreateRequest;
import com.tobias.des.dto.requests.CommentUpdateRequest;
import com.tobias.des.dto.responses.CommentResponse;
import com.tobias.des.entity.Comment;
import com.tobias.des.entity.Post;
import com.tobias.des.entity.User;
import com.tobias.des.repository.CommentRepository;
import com.tobias.des.repository.PostRepository;

import jakarta.transaction.Transactional;

@Service
public class CommentService {

	private CommentRepository commentRepository;
	private UserManager userManager;
	private PostService postService;
	private PostRepository postRepository;

	public CommentService(CommentRepository commentRepository, UserManager userManager, @Lazy PostService postService,
			PostRepository postRepository) {
		super();
		this.commentRepository = commentRepository;
		this.userManager = userManager;
		this.postService = postService;
		this.postRepository = postRepository;
	}

	public List<Comment> getAllCommentsWithParam(Optional<Long> userId, Optional<Long> postId) {
		if (userId.isPresent() && postId.isPresent()) {
			return commentRepository.findByUserIdAndPostId(userId.get(), postId.get());
		} else if (userId.isPresent()) {
			return commentRepository.findByUserId(userId.get());
		} else if (postId.isPresent()) {
			return commentRepository.findByPostId(postId.get());
		} else
			return commentRepository.findAll();

	}

	public List<Comment> getAllCommentsWithParam(Optional<Long> userId) {
		if (userId.isPresent()) {
			return commentRepository.findByUserId(userId.get());
		} else if (userId.isPresent()) {
			return commentRepository.findByUserId(userId.get());
		} else
			return commentRepository.findAll();

	}

	public Comment getOneCommentById(Long commentId) {
		return commentRepository.findById(commentId).orElse(null);
	}

	public Comment createOneComment(CommentCreateRequest request) {
		User user = userManager.getOneUserById(request.getUserId());
		Post post = postService.getOnePostById(request.getPostId());
		if (user != null && post != null) {
			Comment commentToSave = new Comment();
			commentToSave.setId(request.getId());
			commentToSave.setPost(post);
			commentToSave.setUser(user);
			commentToSave.setText(request.getText());
			return commentRepository.save(commentToSave);
		} else
			return null;
	}

	public Comment updateOneCommentById(Long commentId, CommentUpdateRequest request) {
		Optional<Comment> comment = commentRepository.findById(commentId);
		if (comment.isPresent()) {
			Comment commentToUpdate = comment.get();
			commentToUpdate.setText(request.getText());
			return commentRepository.save(commentToUpdate);

		} else
			return null;
	}

	/*
	 * @Transactional public void deleteComment(Long userId, Long postId, Long
	 * commentId) { commentRepository.deleteByUserIdAndPostIdAndId(userId, postId,
	 * commentId); }
	 */
	@Transactional
	public void deleteComment(Long userId, Long postId, Long commentId) throws Exception {
		Optional<Comment> commentOptional = commentRepository.findById(commentId);
		if (commentOptional.isPresent()) {
			Comment comment = commentOptional.get();
			if (comment.getUser().getId().equals(userId) || comment.getPost().getUser().getId().equals(userId)) {
				commentRepository.delete(comment);
			} else {
				// Kullanıcı bu yorumu silme yetkisine sahip değil
				throw new Exception("Bu yorumu silme yetkiniz yok.");
			}
		} else {
			// Verilen commentId'ye sahip yorum bulunamadı
			throw new Exception("Verilen ID'ye sahip yorum bulunamadı.");
		}
	}

	public List<Comment> getCommentsPostId(Long postId) {

		return commentRepository.findByPostId(postId);
	}

	public List<CommentResponse> getCommentsResponseByPostId(Long postId) {
		List<Comment> comments = commentRepository.findByPostId(postId);
		return comments.stream().map(CommentResponse::new).collect(Collectors.toList());
	}

}
