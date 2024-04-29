package com.tobias.des.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tobias.des.dto.JwtAuthResponse;
import com.tobias.des.dto.LoginDto;
import com.tobias.des.dto.PasswordResetRequest;
import com.tobias.des.dto.SignupDto;
import com.tobias.des.dto.responses.GenericResponse;
import com.tobias.des.dto.responses.JwtSignupResponse;
import com.tobias.des.entity.User;
import com.tobias.des.jwt.JwtTokenProvider;
import com.tobias.des.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService userService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/signin")
	public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
		String token = userService.login(loginDto);
		System.out.println(loginDto.getPassword());
		Long userId = userService.getUserIdByUsername(loginDto.getUsernameOrEmail());
		JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
		jwtAuthResponse.setAccessToken(token);
		jwtAuthResponse.setUserId(userId);
		return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
	}

	@PostMapping("/signup")
	public ResponseEntity<JwtSignupResponse> signup(@RequestBody SignupDto signupDto) {
		boolean isUserExist = userService.isUserExist(signupDto.getUsername());

		if (isUserExist) {
			JwtSignupResponse response = new JwtSignupResponse();
			response.setMessage("User already exists");
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}

		userService.signupAndAssignRole(signupDto, "ROLE_USER");

		JwtSignupResponse jwtSignupResponse = new JwtSignupResponse();
		jwtSignupResponse.setMessage("User registered successfully!");
		return new ResponseEntity<>(jwtSignupResponse, HttpStatus.CREATED);
	}

	@PostMapping("/forgotpassword")
	public ResponseEntity<GenericResponse> forgotPassword(@RequestBody PasswordResetRequest request) {
		// Kullanıcı adı veya e-posta ile kullanıcıyı bul
		User user = userService.findByUsernameOrEmail(request.getUsernameOrEmail());

		// Kullanıcı bulunamazsa hata döndür
		if (user == null) {
			throw new UsernameNotFoundException("User not found");
		}

		// Şifre hatırlatma bilgisini kontrol et
		if (!user.getPasswordReminder().equals(request.getPasswordReminder())) {
			return new ResponseEntity<>(new GenericResponse("Incorrect password reminder"), HttpStatus.BAD_REQUEST);
		}

		// Kullanıcının girdiği yeni şifreyi kullanarak şifreyi sıfırla
		userService.resetPassword(user, request.getPassword());

		return new ResponseEntity<>(new GenericResponse("Password reset successfully"), HttpStatus.OK);
	}

}
