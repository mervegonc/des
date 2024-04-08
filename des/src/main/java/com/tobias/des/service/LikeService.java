package com.tobias.des.service;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;

import com.tobias.des.dto.requests.LikeCreateRequest;
import com.tobias.des.dto.responses.LikeResponse;
import com.tobias.des.entity.Like;
import com.tobias.des.entity.Post;
import com.tobias.des.entity.User;
import com.tobias.des.repository.LikeRepository;

import jakarta.transaction.Transactional;

@Service
@Lazy
public class LikeService {

	private LikeRepository likeRepository;
	private UserManager authManager;
	private PostService postService;

	public LikeService(LikeRepository likeRepository, UserManager authManager, PostService postService) {
		super();
		this.likeRepository = likeRepository;
		this.authManager = authManager;
		this.postService = postService;
	}
	/*
	 * public Like createOneLike(LikeCreateRequest request) { User user =
	 * authManager.getOneUserById(request.getUserId()); Post post =
	 * postService.getOnePostById(request.getPostId()); if (user != null && post !=
	 * null) { Like likeToSave = new Like(); likeToSave.setId(request.getId());
	 * likeToSave.setPost(post); likeToSave.setUser(user); return
	 * likeRepository.save(likeToSave); } else return null; }
	 */

	/*
	 * public Like createOneLike(LikeCreateRequest request) { // Kullanıcı ve post
	 * bilgilerini al User user = authManager.getOneUserById(request.getUserId());
	 * Post post = postService.getOnePostById(request.getPostId());
	 * 
	 * // Kullanıcının daha önce bu postu beğenip beğenmediğini kontrol et boolean
	 * alreadyLiked = likeRepository.existsByUserIdAndPostId(user.getId(),
	 * post.getId());
	 * 
	 * // Eğer kullanıcı daha önce bu postu beğenmişse işlemi gerçekleştirme if
	 * (alreadyLiked) { return null; } else { // Yeni bir Like oluştur ve kaydet
	 * Like likeToSave = new Like(); likeToSave.setId(request.getId());
	 * likeToSave.setPost(post); likeToSave.setUser(user); return
	 * likeRepository.save(likeToSave); } }
	 */
	public Like createOneLike(LikeCreateRequest request) {
		// Kullanıcı ve post bilgilerini al
		User user = authManager.getOneUserById(request.getUserId());
		Post post = postService.getOnePostById(request.getPostId());

		// Kullanıcının daha önce bu postu beğenip beğenmediğini kontrol et
		boolean alreadyLiked = likeRepository.existsByUserIdAndPostId(user.getId(), post.getId());

		// Eğer kullanıcı daha önce bu postu beğenmişse işlemi gerçekleştirme
		if (alreadyLiked) {
			return null;
		} else {
			// Yeni bir Like oluştur ve kaydet
			Like likeToSave = new Like();
			likeToSave.setId(request.getId());
			likeToSave.setPost(post);
			likeToSave.setUser(user); // Burada like'ı atan kullanıcının ID'sini ekleyin
			return likeRepository.save(likeToSave);
		}
	}

	public boolean hasUserLikedPost(Long userId, Long postId) {
		return likeRepository.existsByUserIdAndPostId(userId, postId);
	}

	public Like getOneLikeById(Long likeId) {
		return likeRepository.findById(likeId).orElse(null);
	}

	/*
	 * public void deleteOneLikeById(Long userId, Long postId) {
	 * likeRepository.deleteById(userId, postId);
	 * 
	 * }
	 */
	@Transactional
	public void deleteOneLikeById(Long userId, Long postId) {
		likeRepository.deleteById(userId, postId);
	}

	public List<LikeResponse> getAllLikesWithParam(Optional<Long> userId, Optional<Long> postId) {
		List<Like> list;
		if (userId.isPresent() && postId.isPresent()) {
			list = likeRepository.findByUserIdAndPostId(userId.get(), postId.get());
		} else if (userId.isPresent()) {
			list = likeRepository.findByUserId(userId.get());
		} else if (postId.isPresent()) {
			list = likeRepository.findByPostId(postId.get());
		} else
			list = likeRepository.findAll();
		return list.stream().map(like -> new LikeResponse(like)).collect(Collectors.toList());
	}

	public Long getTotalLikesForPostId(Long postId) {
		List<Like> likesForPost = likeRepository.findByPostId(postId);
		return (long) likesForPost.size();
	}

	public Long getLikeIdByUserIdAndPostId(Long userId, Long postId) {
		return likeRepository.findIdByUserIdAndPostId(userId, postId);
	}

	public List<LikeResponse> getAllLikesForPostId(Optional<Long> postId) {
		if (postId.isPresent()) {
			List<Like> likesForPost = likeRepository.findByPostId(postId.get());
			return likesForPost.stream().map(like -> new LikeResponse(like)).collect(Collectors.toList());
		} else {
			// postId belirtilmemişse veya bulunamamışsa boş bir liste döndür
			return Collections.emptyList();
		}
	}

}
