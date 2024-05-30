package com.tobias.des.controller;

import java.util.List;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tobias.des.dto.requests.CommentCreateRequest;
import com.tobias.des.dto.requests.CommentUpdateRequest;
import com.tobias.des.dto.responses.CommentResponse;
import com.tobias.des.entity.Comment;
import com.tobias.des.service.CommentService;

@RestController
@RequestMapping("/api/comment")
public class CommentController {

	private CommentService commentService;

	public CommentController(CommentService commentService) {
		super();
		this.commentService = commentService;
	}

	@PostMapping
	public Comment createOneComment(@RequestBody CommentCreateRequest request) {
		return commentService.createOneComment(request);
	}

	@PutMapping("/{commentId}")
	public Comment updateOneComment(@PathVariable Long commentId, @RequestBody CommentUpdateRequest request) {
		return commentService.updateOneCommentById(commentId, request);
	}

	@DeleteMapping("/{userId}/{postId}/{commentId}")
	public void deleteComment(@PathVariable Long userId, @PathVariable Long postId, @PathVariable Long commentId)
			throws Exception {
		commentService.deleteComment(userId, postId, commentId);
	}

	@GetMapping("/post/{postId}/comment")
	public List<CommentResponse> usersCommentsByPostId(@PathVariable Long postId) {
		return commentService.getCommentsResponseByPostId(postId);
	}

}
