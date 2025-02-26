package zve.com.vn.controller;

import java.util.List;

import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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

import lombok.extern.slf4j.Slf4j;
import zve.com.vn.dto.request.UserRequest;
import zve.com.vn.dto.request.UserUpdateRequest;
import zve.com.vn.dto.response.ApiResponse;
import zve.com.vn.dto.response.UserResponse;
import zve.com.vn.enums.ErrorCode;
import zve.com.vn.service.UserService;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {
  /* ------------------------------------------------------------------ */
  @Autowired private UserService userService;

  /* ------------------------------------------------------------------ */
  @PostMapping
  public ApiResponse<UserResponse> createUser(@RequestBody @Valid UserRequest request) {
    log.info("-------------------------------------------------");
    log.info("Controller: create User");
    log.info("-------------------------------------------------");
    ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
    apiResponse.setResult(userService.createUser(request));
    apiResponse.setCode(ErrorCode.USER_CREATE_SUCCESS.getCode());
    apiResponse.setMessage(ErrorCode.USER_CREATE_SUCCESS.getMessage());
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @GetMapping("")
  public List<UserResponse> getUsers() {
    return userService.getUsers();
  }

  /* ------------------------------------------------------------------ */

  @GetMapping("/{userId}")
  public UserResponse getUserById(@PathVariable("userId") String userId) {
    return userService.getUserById(userId);
  }

  /* ------------------------------------------------------------------ */
  @GetMapping("/search")
  public ResponseEntity<List<UserResponse>> searchUser(
      @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {
    List<UserResponse> users = userService.searchUser(keyword);
    return ResponseEntity.ok(users);
  }

  /* ------------------------------------------------------------------ */
  @PutMapping("/{userId}")
  public ApiResponse<UserResponse> updateUser(
      @PathVariable("userId") String userId, @RequestBody @Valid UserUpdateRequest req) {
    ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
    apiResponse.setResult(userService.updateUser(userId, req));
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @DeleteMapping("/{userId}")
  public String deleteUserById(@PathVariable("userId") String userId) {
    userService.deleteUser(userId);
    return "User with id: " + userId + "have been deleted";
  }

  /* ------------------------------------------------------------------ */
  @GetMapping("/info")
  public ApiResponse<UserResponse> userInfo() {
    ApiResponse<UserResponse> apiResponse = new ApiResponse<>();
    apiResponse.setResult(userService.userInfo());
    return apiResponse;
  }
  /* ------------------------------------------------------------------ */
}
