package zve.com.vn.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import zve.com.vn.dto.mapper.UserMapper;
import zve.com.vn.dto.request.UserCreationRequest;
import zve.com.vn.dto.request.UserUpdateRequest;
import zve.com.vn.dto.response.UserResponse;
import zve.com.vn.entity.User;
import zve.com.vn.enums.ErrorCode;
import zve.com.vn.exception.CustomAppException;
import zve.com.vn.repository.UserRepository;


@Service
public class UserService {
	
	/* ------------------------------------------------------------------ */
	@Autowired
	private UserRepository userRepository;
	
	@Autowired
	private UserMapper userMapper;
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	/* ------------------------------------------------------------------ */
	public UserResponse createUser(UserCreationRequest request) {
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new CustomAppException(ErrorCode.USER_EXISTED);
		} 
		
		else if (userRepository.existsByEmail(request.getEmail())) {
			throw new CustomAppException(ErrorCode.EMAIL_EXISTED);
		}

		User user = userMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		//user.setPassword(BCrypt.hashpw(request.getPassword(), BCrypt.gensalt(12)));
		//user.setPassword(new BCryptPasswordEncoder().encode(request.getPassword()));
		
		/*
		HashSet<String> roles = new HashSet<>();
		roles.add(Role.USER.name());
		user.setRoles(roles);
		*/
		userRepository.save(user);
		return userMapper.toUserResponse(userRepository.save(user));
	}
	/* ------------------------------------------------------------------ */
	//@PreAuthorize("hasRole('ADMIN')")
	public List<UserResponse> getUsers() {
		List<UserResponse> result =  new ArrayList<UserResponse>();
		Sort sort = Sort.by(Sort.Direction.ASC, "username");
		
		for(User user: userRepository.findAll(sort)) {
			result.add(userMapper.toUserResponse(user));
		}
		return result;
	}
	/* ------------------------------------------------------------------ */
	public List<UserResponse> searchUser(String keyword) {
		List<UserResponse> result = new ArrayList<>();

		for (User user: userRepository.findAll()) {
			if(user.getFirstName() != null && user.getFirstName().contains(keyword)) {
				result.add(userMapper.toUserResponse(user));
			}
		}
		return result;
	}
	/* ------------------------------------------------------------------ */
	@PostAuthorize("returnObject.username == authentication.name")
	public UserResponse getUserById(String id) {
		return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User ko tồn tại")))  ;
	}
	/* ------------------------------------------------------------------ */
	public User updateUser(String userId, UserUpdateRequest request) {
		
		User user = userRepository.findById(userId).orElseThrow(() -> new RuntimeException("User ko tồn tại"));
		return userRepository.save(user);
		
	}
	/* ------------------------------------------------------------------ */
	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}
	/* ------------------------------------------------------------------ */
	public UserResponse userInfo() {
		var context = SecurityContextHolder.getContext();
		String name = context.getAuthentication().getName();
		User user = userRepository.findByUsername(name).orElseThrow(() -> new RuntimeException("User ko ton tai"));
		UserResponse userResponse = userMapper.toUserResponse(user);
		return userResponse;
	}
	/* ------------------------------------------------------------------ */
}
