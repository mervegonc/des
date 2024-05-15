package com.tobias.des.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.tobias.des.dto.Constants;
import com.tobias.des.dto.LoginDto;
import com.tobias.des.dto.SignupDto;
import com.tobias.des.entity.Role;
import com.tobias.des.entity.User;
import com.tobias.des.jwt.JwtTokenProvider;
import com.tobias.des.repository.RoleRepository;
import com.tobias.des.repository.UserRepository;

@Service
public class UserManager implements UserService {
	private static final String PROFILE_UPLOAD_DIR = "C:/campspring/des/src/main/java/com/tobias/des/uploads/";
	private final UserRepository userRepository;
	private final RoleRepository roleRepository;
	private final PasswordEncoder passwordEncoder;
	private final AuthenticationManager authenticationManager;
	private final JwtTokenProvider jwtTokenProvider;

	public UserManager(UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder,
			AuthenticationManager authenticationManager, JwtTokenProvider jwtTokenProvider) {
		this.userRepository = userRepository;
		this.roleRepository = roleRepository;
		this.passwordEncoder = passwordEncoder;
		this.authenticationManager = authenticationManager;
		this.jwtTokenProvider = jwtTokenProvider;

	}

	@Override
	public List<User> searchByUsername(String username) {
		return userRepository.searchFindByUsername(username);
	}

	@Override
	public String login(LoginDto loginDto) {

		Authentication authentication = authenticationManager.authenticate(
				new UsernamePasswordAuthenticationToken(loginDto.getUsernameOrEmail(), loginDto.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		String token = jwtTokenProvider.generateToken(authentication);

		return token;
	}

	@Override
	public void signup(SignupDto signupDto) {
		if (isUserExist(signupDto.getUsername())) {
			throw new RuntimeException("User already exists!");
		}

		User user = new User();
		user.setName(signupDto.getName());
		user.setUsername(signupDto.getUsername());
		user.setEmail(signupDto.getEmail());
		user.setPassword(passwordEncoder.encode(signupDto.getPassword()));

		Role userRole = roleRepository.findByName("USER");
		Set<Role> roles = new HashSet<>();
		roles.add(userRole);
		user.setRoles(roles);

		userRepository.save(user);
	}

	@Override
	public void signupAndAssignRole(SignupDto signupDto, String roleName) {
		User user = new User();
		user.setName(signupDto.getName());
		user.setUsername(signupDto.getUsername());
		user.setEmail(signupDto.getEmail());
		user.setPasswordReminder(signupDto.getPasswordReminder());
		user.setPassword(passwordEncoder.encode(signupDto.getPassword()));

		Role userRole = roleRepository.findByName(roleName);
		if (userRole == null) {
			throw new RuntimeException("Role not found: " + roleName);
		}

		Set<Role> roles = new HashSet<>();
		roles.add(userRole);
		user.setRoles(roles);

		userRepository.save(user);
	}

	@Override
	public boolean isUserExist(String username) {
		return userRepository.existsByUsername(username);
	}

	@Override
	public User getOneUserById(Long userId) {
		return userRepository.findById(userId).orElse(null);
	}

	@Override
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}

	@Override
	public User saveOneUser(User newUser) {
		return userRepository.save(newUser);
	}

	@Override
	public User updateOneUser(Long userId, User newUser) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			User foundUser = user.get();
			foundUser.setUsername(newUser.getUsername());
			foundUser.setPassword(newUser.getPassword());
			userRepository.save(foundUser);
			return foundUser;
		} else {
			return null;
		}
	}

	@Override
	public User updateInfo(Long userId, User newUserInfo) {
		Optional<User> user = userRepository.findById(userId);
		if (user.isPresent()) {
			User foundUser = user.get();
			// Yeni kullanıcı bilgilerini güncelle

			foundUser.setName(newUserInfo.getName());
			foundUser.setBio(newUserInfo.getBio());

			foundUser.setGender(newUserInfo.getGender());
			foundUser.setConnections(newUserInfo.getConnections()); // connections alanını da güncelle
			// userRepository aracılığıyla değişiklikleri veritabanına kaydet
			userRepository.save(foundUser);
			return foundUser;
		} else {
			return null;
		}
	}

	@Override
	public void deleteOneUser(Long userId) {
		userRepository.deleteById(userId);
	}

	@Override
	public Long getUserIdByUsername(String usernameOrEmail) {
		User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail).orElseThrow(
				() -> new UsernameNotFoundException("User not found by username or email: " + usernameOrEmail));
		return user.getId();
	}

	private static final String UPLOADS_DIR = "C:/campspring/des/src/main/java/com/tobias/des/uploads/profile";

	public Resource getUserProfilePhoto(Long userId) throws IOException {
		Path filePath = Paths.get(Constants.USER_PROFILE + userId + ".jpg");
		Resource resource = new PathResource(filePath);
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return resource;
		} else {
			throw new IOException("User profile photo not found for user id: " + userId);
		}
	}

	private static final String PROFILE_BACKGROUND_DIR = "C:/campspring/des/src/main/java/com/tobias/des/uploads/background";

	public Resource getUserBackgroundPhoto(Long userId) throws IOException {
		Path filePath = Paths.get(Constants.BACKGROUND_DIR + userId + ".jpg");
		Resource resource = new PathResource(filePath);
		if (Files.exists(filePath) && Files.isReadable(filePath)) {
			return resource;
		} else {
			throw new IOException("Background photo not found for user id: " + userId);
		}
	}

	@Override
	public String uploadUserBackgroundPhoto(Long userId, MultipartFile file) throws Exception {
		try {
			// Dosyanın kaydedileceği dizin ve dosya adını oluştur
			Path uploadPath = Paths.get(Constants.BACKGROUND_DIR);
			String fileName = userId + ".jpg";

			// Dosyayı belirtilen dizine kaydet
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// Dosyanın adını döndür
			return fileName;
		} catch (IOException e) {
			throw new Exception("Failed to store file " + file.getOriginalFilename(), e);
		}
	}

	@Override
	public String uploadUserPhoto(Long userId, MultipartFile file) throws Exception {
		try {
			// Dosyanın kaydedileceği dizin ve dosya adını oluştur
			Path uploadPath = Paths.get(Constants.USER_PROFILE);
			String fileName = userId + ".jpg";

			// Dosyayı belirtilen dizine kaydet
			Path filePath = uploadPath.resolve(fileName);
			Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

			// Dosyanın adını döndür
			return fileName;
		} catch (IOException e) {
			throw new Exception("Failed to store file " + file.getOriginalFilename(), e);
		}
	}

	@Override
	public User findByUsernameOrEmail(String usernameOrEmail) {
		return userRepository.findByUsername(usernameOrEmail);
	}

	@Override
	public void resetPassword(User user, String newPassword) {
		// Yeni şifreyi şifreleyerek kullanıcı nesnesine ayarlayın
		user.setPassword(passwordEncoder.encode(newPassword));

		// Şifre hatırlatma alanını boşaltın (isteğe bağlı)
		/* user.setPasswordReminder(pass); */

		// Kullanıcıyı güncelleyin
		userRepository.save(user);
	}

	@Override
	public User findById(Long userId) {
		// TODO Auto-generated method stub
		return null;
	}

}