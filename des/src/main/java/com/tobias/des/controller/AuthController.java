package com.tobias.des.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.tobias.des.dto.JwtAuthResponse;
import com.tobias.des.dto.LoginDto;
import com.tobias.des.dto.SignupDto;
import com.tobias.des.dto.responses.JwtSignupResponse;
import com.tobias.des.jwt.JwtTokenProvider;
import com.tobias.des.service.UserService;

import lombok.AllArgsConstructor;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {

	private final UserService authService;
	private final JwtTokenProvider jwtTokenProvider;

	@PostMapping("/signin")
	public ResponseEntity<JwtAuthResponse> login(@RequestBody LoginDto loginDto) {
		String token = authService.login(loginDto);
		System.out.println(loginDto.getPassword());
		Long userId = authService.getUserIdByUsername(loginDto.getUsernameOrEmail());
		JwtAuthResponse jwtAuthResponse = new JwtAuthResponse();
		jwtAuthResponse.setAccessToken(token);
		jwtAuthResponse.setUserId(userId);
		return new ResponseEntity<>(jwtAuthResponse, HttpStatus.OK);
	}

	@PostMapping("/signup")
	public ResponseEntity<JwtSignupResponse> signup(@RequestBody SignupDto signupDto) {
		boolean isUserExist = authService.isUserExist(signupDto.getUsername());

		if (isUserExist) {
			JwtSignupResponse response = new JwtSignupResponse();
			response.setMessage("User already exists");
			return new ResponseEntity<>(response, HttpStatus.CONFLICT);
		}

		authService.signupAndAssignRole(signupDto, "ROLE_USER");

		JwtSignupResponse jwtSignupResponse = new JwtSignupResponse();
		jwtSignupResponse.setMessage("User registered successfully!");
		return new ResponseEntity<>(jwtSignupResponse, HttpStatus.CREATED);
	}
}
