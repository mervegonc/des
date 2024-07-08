package com.tobias.des.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.core.io.PathResource;
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

import com.tobias.des.dto.responses.UserResponse;
import com.tobias.des.entity.User;
import com.tobias.des.entity.UserFollower;
import com.tobias.des.service.UserDetailsServiceImpl;
import com.tobias.des.service.UserManager;

@RestController
@RequestMapping("/api/user")
public class UserController {

	private final UserManager userManager;
	private final UserDetailsServiceImpl userDetailsService;

	public UserController(UserManager userManager, UserDetailsServiceImpl userDetailsService) {
		this.userManager = userManager;
		this.userDetailsService = userDetailsService;
	}

	@GetMapping("/search")
	public List<User> searchUsers(@RequestParam("username") String username) {
		return userManager.searchByUsername(username);
	}

	@GetMapping
	public List<UserResponse> getAllUsers() {
		List<User> users = userManager.getAllUsers();
		return users.stream().map(userManager::convertToUserResponse).collect(Collectors.toList());
	}

	@GetMapping("/me")
	public ResponseEntity<UserResponse> getCurrentUser() {
		return userManager.getCurrentUser();
	}

	@GetMapping("/{userId}")
	public ResponseEntity<UserResponse> getOneUserById(@PathVariable Long userId) {
		User user = userManager.getOneUserById(userId);
		if (user != null) {
			UserResponse userResponse = userManager.convertToUserResponse(user);
			return ResponseEntity.ok(userResponse);
		} else {
			return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
		}
	}

	@GetMapping("/backgrounds/{userId}")
	public ResponseEntity<Resource> getUserBackgroundPhoto(@PathVariable Long userId) {
		try {
			Resource photo = userManager.getUserBackgroundPhoto(userId);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"background" + userId + ".jpg\"")
					.body(photo);
		} catch (IOException e) {
			Resource defaultPhoto = new PathResource(
					Paths.get("/home/ubuntu/des/des/src/main/resources/uploads/background/background.png"));
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"blank.png\"").body(defaultPhoto);
		}
	}

	@GetMapping("/profile/{userId}")
	public ResponseEntity<Resource> getUserProfilePhoto(@PathVariable Long userId) {
		try {
			Resource photo = userManager.getUserProfilePhoto(userId);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + photo.getFilename() + "\"")
					.body(photo);
		} catch (IOException e) {
			Resource defaultPhoto = new PathResource(
					Paths.get("/home/ubuntu/des/des/src/main/resources/uploads/profile/blank.png"));
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"blank.png\"").body(defaultPhoto);
		}
	}

//There was a file directory error regarding a photo here, it has been resolved
	@PostMapping
	public User createUser(@RequestBody User newUser) {
		return userManager.saveOneUser(newUser);
	}

	@PutMapping("/{userId}")
	public User updateOneUser(@PathVariable Long userId, @RequestBody User newUser) {
		return userManager.updateOneUser(userId, newUser);
	}

	@PutMapping("/info/{userId}")
	public User updateInfo(@PathVariable Long userId, @RequestBody User newUserInfo) {
		return userManager.updateInfo(userId, newUserInfo);
	}

	@DeleteMapping("/{userId}")
	public void deleteOneUser(@PathVariable Long userId) {
		userManager.deleteOneUser(userId);
	}

	@PutMapping("/photo/{userId}")
	public ResponseEntity<String> updateUserPhoto(@PathVariable Long userId, @RequestParam("file") MultipartFile file) {
		try {
			String photoName = userManager.uploadUserPhoto(userId, file);
			return ResponseEntity.ok().body("Profil fotoğrafı başarıyla güncellendi. Dosya adı: " + photoName);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Dosya yüklenirken bir hata oluştu");
		}
	}

	@PutMapping("/background/{userId}")
	public ResponseEntity<String> updateUserBackgroundPhoto(@PathVariable Long userId,

			@RequestParam("file") MultipartFile file) {
		try {
			String photoName = userManager.uploadUserBackgroundPhoto(userId, file);
			return ResponseEntity.ok()
					.body("Profil Background fotoğrafı başarıyla güncellendi. Dosya adı: " + photoName);
		} catch (Exception e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Dosya yüklenirken bir hata oluştu");
		}
	}

	@GetMapping("/{userId}/followers")
	public ResponseEntity<Map<String, Object>> getFollowers(@PathVariable Long userId) {
		List<UserFollower> followers = userManager.getFollowers(userId);
		List<Long> followerIds = followers.stream().map(follower -> follower.getFollower().getId())
				.collect(Collectors.toList());
		return ResponseEntity.ok(Map.of("followers", followerIds, "total", followerIds.size()));
	}

	// Yeni endpoint
	@GetMapping("/{userId}/isFollowing/{followerId}")
	public ResponseEntity<Boolean> isFollowing(@PathVariable Long userId, @PathVariable Long followerId) {
		boolean isFollowing = userManager.isFollowing(userId, followerId);
		return ResponseEntity.ok(isFollowing);
	}

	@GetMapping("/{userId}/following")
	public ResponseEntity<Map<String, Object>> getFollowing(@PathVariable Long userId) {
		List<UserFollower> following = userManager.getFollowing(userId);
		List<Long> followingIds = following.stream().map(followingUser -> followingUser.getUser().getId())
				.collect(Collectors.toList());
		return ResponseEntity.ok(Map.of("following", followingIds, "total", followingIds.size()));
	}

	@PostMapping("/{userId}/follow/{followerId}")
	public ResponseEntity<String> followUser(@PathVariable Long userId, @PathVariable Long followerId) {
		userManager.followUser(userId, followerId);
		return ResponseEntity.ok("User followed successfully");
	}

	@DeleteMapping("/{userId}/unfollow/{followerId}")
	public ResponseEntity<String> unfollowUser(@PathVariable Long userId, @PathVariable Long followerId) {
		userManager.unfollowUser(userId, followerId);
		return ResponseEntity.ok("User unfollowed successfully");
	}
}
