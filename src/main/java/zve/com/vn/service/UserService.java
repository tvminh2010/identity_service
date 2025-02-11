package zve.com.vn.service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PostAuthorize;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import zve.com.vn.dto.mapper.UserMapper;
import zve.com.vn.dto.request.UserRequest;
import zve.com.vn.dto.request.UserUpdateRequest;
import zve.com.vn.dto.response.UserResponse;
import zve.com.vn.entity.Role;
import zve.com.vn.entity.User;
import zve.com.vn.enums.ErrorCode;
import zve.com.vn.exception.CustomAppException;
import zve.com.vn.repository.RoleRepository;
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
	
	@Autowired
	private RoleRepository roleRepository;
	
	
	/* ------------------------------------------------------------------ */
	public UserResponse createUser(UserRequest request) {
		if(userRepository.existsByUsername(request.getUsername())) {
			throw new CustomAppException(ErrorCode.USER_EXISTED);
		} 
		
		if (userRepository.existsByEmail(request.getEmail())) {
			throw new CustomAppException(ErrorCode.EMAIL_EXISTED);
		}

		User user = userMapper.toUser(request);
		user.setPassword(passwordEncoder.encode(request.getPassword()));
		
		if (request.getRoles() != null && !request.getRoles().isEmpty()) {
			Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoles()));
			user.setRoles(roles);
		}
	      
		return userMapper.toUserResponse(userRepository.save(user));
	}
	/* ------------------------------------------------------------------ */
	//@PreAuthorize("hasRole('ADMIN') or hasRole('USER')")
	@PreAuthorize("hasRole('ADMIN')")
	//@PreAuthorize("hasAuthority('APPROVE_POST')") /* Map chính xác với 1 trong các scope */
	public List<UserResponse> getUsers() {
		Sort sort = Sort.by(Sort.Direction.ASC, "username");
        return userRepository.findAll(sort)
                .stream()
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
	}
	/* ------------------------------------------------------------------ */
	public List<UserResponse> searchUser(String keyword) {
		Sort sort = Sort.by(Sort.Direction.ASC, "username");
		return userRepository.findAll(sort)
                .stream()
                .filter(user -> user.getFirstName() != null && 
                		user.getFirstName().contains(keyword))
                .map(userMapper::toUserResponse)
                .collect(Collectors.toList());
	}
	/* ------------------------------------------------------------------ */
	@PostAuthorize("returnObject.username == authentication.name")
	public UserResponse getUserById(String id) {
		return userMapper.toUserResponse(userRepository.findById(id).orElseThrow(() -> new RuntimeException("User ko tồn tại")))  ;
	}
	/* ------------------------------------------------------------------ */
	public UserResponse updateUser(String userId, UserUpdateRequest request) {
		User existingUser = userRepository
				 	.findById(userId)
	                .orElseThrow(() -> new CustomAppException(ErrorCode.USER_NOTFOUND));
		 
		userMapper.updateUser(existingUser, request);
	
		if (request.getRoles() != null && !request.getRoles().isEmpty()) {
			Set<Role> roles = new HashSet<>(roleRepository.findAllById(request.getRoles()));
			existingUser.setRoles(roles);
		}
		return userMapper.toUserResponse(userRepository.save(existingUser));
	}
	/* ------------------------------------------------------------------ */
	public void deleteUser(String userId) {
		userRepository.deleteById(userId);
	}
	/* ------------------------------------------------------------------ */
	public UserResponse userInfo() {
		String name = SecurityContextHolder.getContext().getAuthentication().getName();
		 return userRepository.findByUsername(name)
				 .map(userMapper::toUserResponse)
				 .orElseThrow(() -> new RuntimeException("User không tồn tại"));
	}
	/* ------------------------------------------------------------------ */
}
