package com.tobias.des.service;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

//AuthService.java

import com.tobias.des.dto.LoginDto;
import com.tobias.des.dto.SignupDto;
import com.tobias.des.dto.responses.UserResponse;
import com.tobias.des.entity.User;
import com.tobias.des.entity.UserFollower;

public interface UserService {

	String login(LoginDto loginDto);

	void signup(SignupDto signupDto);

	void signupAndAssignRole(SignupDto signupDto, String roleName);

	boolean isUserExist(String username);

	public User getOneUserById(Long userId);

	public List<User> getAllUsers();

	public User saveOneUser(User newUser);

	public User updateOneUser(Long userId, User newUser);

	User updateInfo(Long userId, User newUserInfo);

	public void deleteOneUser(Long userId);

	Long getUserIdByUsername(String usernameOrEmail);

	String uploadUserPhoto(Long userId, MultipartFile file) throws Exception;

	String uploadUserBackgroundPhoto(Long userId, MultipartFile file) throws Exception;

	List<User> searchByUsername(String username);

	void resetPassword(User user, String newPassword);

	User findByUsernameOrEmail(String usernameOrEmail);

	User findById(Long userId);

	UserResponse convertToUserResponse(User user);

	List<UserFollower> getFollowers(Long userId);

	void followUser(Long userId, Long followerId);

	void unfollowUser(Long userId, Long followerId);
}