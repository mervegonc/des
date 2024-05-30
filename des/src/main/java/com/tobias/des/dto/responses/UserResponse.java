package com.tobias.des.dto.responses;

import com.tobias.des.entity.User.Gender;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
	private Long id;
	private String name;
	private String username;
	private String email;
	private String bio;
	private String connections;
	private Gender gender;
}
