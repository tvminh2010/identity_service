package zve.com.vn.controller;

import java.time.LocalDate;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatchers;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.extern.slf4j.Slf4j;
import zve.com.vn.dto.request.UserRequest;
import zve.com.vn.dto.response.UserResponse;
import zve.com.vn.service.UserService;

@Slf4j
@SpringBootTest
@AutoConfigureMockMvc
@TestPropertySource("classpath:test.properties")
class UserControllerTest {

  @Autowired private MockMvc mockMvc;

  @MockitoBean // @MockBean (đã bị thay bằng @MockitoBean từ version 3.4.0)
  private UserService userService;

  private UserRequest request;
  private UserResponse response;
  private LocalDate dob;

  @BeforeEach
  void initData() {
    // userService = Mockito.mock(UserService.class);
    request =
        UserRequest.builder()
            .username("john")
            .firstName("John")
            .lastName("Doe")
            .password("Tvminh.2026")
            .dob(dob)
            .build();
    response =
        UserResponse.builder()
            .id("fe7095b376a")
            .username("john")
            .firstName("John")
            .lastName("Doe")
            .dob(dob)
            .build();
    dob = LocalDate.of(1990, 1, 1);
  }

  /* ---------------------------------------------------------------- */
  @Test
  void createUser_validRequest_success() throws Exception {
    // GIVEN: Chuẩn bị dữ liệu đầu vào
    ObjectMapper objectMapper = new ObjectMapper();
    String content = objectMapper.writeValueAsString(request);
    Mockito.when(userService.createUser(ArgumentMatchers.any())).thenReturn(response);

    // WHEN, THEN  ----> (WHEN: Khi gửi api request; THEN: kết quả nhận được)
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
        .andExpect(MockMvcResultMatchers.status().isOk())
        .andExpect(MockMvcResultMatchers.jsonPath("code").value(1001))
        .andExpect(MockMvcResultMatchers.jsonPath("message").value("Tạo user thành công"))
        .andExpect(MockMvcResultMatchers.jsonPath("result.id").value("fe7095b376a"))
        .andExpect(MockMvcResultMatchers.jsonPath("result.username").value("john"));
  }

  /* ---------------------------------------------------------------- */
  @Test
  void createUser_usernameInvalid_fail() throws Exception {
    // GIVEN: Chuẩn bị dữ liệu đầu vào
    request.setUsername("joh");
    ObjectMapper objectMapper = new ObjectMapper();
    String content = objectMapper.writeValueAsString(request);

    // WHEN, THEN  ----> (WHEN: Khi gửi api request; THEN: kết quả nhận được)
    mockMvc
        .perform(
            MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .content(content))
        .andExpect(MockMvcResultMatchers.status().isBadRequest())
        .andExpect(MockMvcResultMatchers.jsonPath("code").value(1006))
        .andExpect(MockMvcResultMatchers.jsonPath("message").value("Username phải >= 4 ký tự"));
  }
}
