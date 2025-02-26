package zve.com.vn.service;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

import java.time.LocalDate;
import java.util.List;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.domain.Sort;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import lombok.extern.slf4j.Slf4j;
import zve.com.vn.dto.request.UserRequest;
import zve.com.vn.dto.response.UserResponse;
import zve.com.vn.entity.User;
import zve.com.vn.exception.CustomAppException;
import zve.com.vn.repository.UserRepository;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
class UserServiceTest {

  @Autowired private UserService userService;

  @MockitoBean
  // @Mock
  private UserRepository userRepository;

  private User user;
  private UserRequest request;
  // private UserResponse userResponse;
  private LocalDate dob;

  @BeforeEach
  void initData() {

    // Khởi tạo dob
    dob = LocalDate.of(1990, 1, 1);

    // Tạo 1 request giả định
    request =
        UserRequest.builder()
            .username("john")
            .firstName("John")
            .email("john@dia-zbr.com.vn")
            .lastName("Doe")
            .password("Tvminh.2026")
            .dob(dob)
            .build();

    // Tạo một response mẫu (UserResponse).
    /* userResponse = UserResponse.builder()
             .id("fe7095b376a")
             .username("john")
             .firstName("John")
             .lastName("Doe")
             .dob(dob)
             .build();
    */

    // Tạo một user entity giả định
    user =
        User.builder()
            .id("fe7095b376a")
            .username("john")
            .firstName("John")
            .lastName("Doe")
            .email("john@dia-zbr.com.vn")
            .dob(dob)
            .build();
  }

  /* ---------------------------------------------------------------- */
  /**
   * Kiểm thử với trường hợp thành công
   *
   * @throws Exception
   */
  @Test
  void createUser_Success() {
    // GIVEN: Chuẩn bị dữ liệu đầu vào
    // Giả lập user chưa tồn tại trong CSDL
    Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(false);
    Mockito.when(userRepository.save(any())).thenReturn(user);

    // WHEN, gọi service để tạo user
    UserResponse response = userService.createUser(request);

    // THEN, kiểm tra kết quả
    Assertions.assertThat(response.getId()).isEqualTo("fe7095b376a");
    Assertions.assertThat(response.getUsername()).isEqualTo("john");
    Assertions.assertThat(response.getFirstName()).isEqualTo("John");
    Assertions.assertThat(response.getLastName()).isEqualTo("Doe");
    // Assertions.assertThat(response.getDob()).isEqualTo("dob");
  }

  /* ---------------------------------------------------------------- */
  @Test
  void createUser_Fail_UserAlreadyExists() throws Exception {
    // GIVEN: Giả lập user đã tồn tại trong CSDL
    Mockito.when(userRepository.existsByUsername(anyString())).thenReturn(true);

    // WHEN
    CustomAppException exception =
        assertThrows(CustomAppException.class, () -> userService.createUser(request));

    // THEN
    Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1004);
  }

  /* ---------------------------------------------------------------- */
  @Test
  void createUser_emailInvalid_fail() {
    // GIVEN: Giả lập email đã tồn tại trong CSDL
    Mockito.when(userRepository.existsByEmail(anyString())).thenReturn(true);

    // WHEN
    CustomAppException exception =
        assertThrows(CustomAppException.class, () -> userService.createUser(request));

    // THEN
    Assertions.assertThat(exception.getErrorCode().getCode()).isEqualTo(1005);
    Assertions.assertThat(exception.getErrorCode().getMessage()).isEqualTo("Email đã tồn tại");
  }

  /* ---------------------------------------------------------------- */
  @Test
  @WithMockUser(
      username = "admin",
      roles = {"ADMIN"})
  void getAllUsers_ReturnsListOfUserResponse() {
    // GIVEN: Chuẩn bị danh sách user giả lập
    List<User> mockUsers =
        List.of(
            User.builder()
                .id("1")
                .username("john")
                .firstName("John")
                .lastName("Doe")
                .dob(LocalDate.of(1990, 1, 1))
                .build(),
            User.builder()
                .id("2")
                .username("jane")
                .firstName("Jane")
                .lastName("Doe")
                .dob(LocalDate.of(1995, 5, 15))
                .build());

    // Giả lập repository trả về danh sách user
    Mockito.when(userRepository.findAll(Mockito.any(Sort.class))).thenReturn(mockUsers);

    // WHEN: Gọi service để lấy danh sách user
    List<UserResponse> result = userService.getUsers();
    System.out.println("Users returned from service: " + result);

    // THEN: Kiểm tra danh sách trả về
    Assertions.assertThat(result).isNotNull().hasSize(2);
    Assertions.assertThat(result.get(0).getUsername()).isEqualTo("john");
    Assertions.assertThat(result.get(1).getUsername()).isEqualTo("jane");
  }
  /* ---------------------------------------------------------------- */
}
