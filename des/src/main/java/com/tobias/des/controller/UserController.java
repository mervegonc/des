package com.tobias.des.controller;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
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

import com.tobias.des.entity.User;
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

	@GetMapping
	public List<User> getAllUsers() {
		return userManager.getAllUsers();
	}

	@PostMapping
	public User createUser(@RequestBody User newUser) {
		return userManager.saveOneUser(newUser);
	}

	@GetMapping("/{userId}")
	public User getOneUserById(@PathVariable Long userId) {
		return userManager.getOneUserById(userId);
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

	@GetMapping("/me")
	public ResponseEntity<User> getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		if (authentication == null || !authentication.isAuthenticated()) {
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
		}

		UserDetails userDetails = (UserDetails) authentication.getPrincipal();
		String usernameOrEmail = userDetails.getUsername();
		Long userId = userDetailsService.getUserIdByUsernameOrEmail(usernameOrEmail);

		if (userId == null) {
			throw new UsernameNotFoundException("User not found with username: " + usernameOrEmail);
		}

		User user = userDetailsService.getOneUserById(userId);
		return ResponseEntity.ok(user);
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

	@GetMapping("/backgrounds/{userId}")
	public ResponseEntity<Resource> getUserBackgroundPhoto(@PathVariable Long userId) {
		try {
			Resource photo = userManager.getUserBackgroundPhoto(userId);
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"background" + userId + ".jpg\"")
					.body(photo);
		} catch (IOException e) {
			Resource defaultPhoto = new PathResource(
					Paths.get("C:/campspring/des/src/main/java/com/tobias/des/uploads/background/background.png"));
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
					Paths.get("C:/campspring/des/src/main/java/com/tobias/des/uploads/profile/blank.png"));
			return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG)
					.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"blank.png\"").body(defaultPhoto);
		}
	}

}
