package zve.com.vn.configuration;

import java.io.IOException;

import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import zve.com.vn.dto.response.ApiResponse;
import zve.com.vn.enums.ErrorCode;
/***
 * AuthenticationEntryPoint được sử dụng khi người dùng không có token hoặc token không hợp lệ,
 * Spring Security sẽ gọi AuthenticationEntryPoint để xử lý lỗi.
 * Nó không kiểm tra hay xác thực token, chỉ đơn thuần trả về phản hồi lỗi.
 */
@Component
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint{
	
	private final ObjectMapper objectMapper;
	
	/* --------------------------------------------------------------------- */
    public JwtAuthenticationEntryPoint(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }
	/* --------------------------------------------------------------------- */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException, ServletException {
		
		ErrorCode errorCode = ErrorCode.UN_AUTHENTICATED;
		response.setStatus(errorCode.getHttpStatusCode().value());
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		
		 //ObjectMapper objectMapper = new ObjectMapper();
         response.getWriter().write(objectMapper.writeValueAsString(buildApiResponse(errorCode)));
         response.flushBuffer();			//ép buộc dữ liệu trong buffer được gửi ngay lập tức đến client
		
	}
	/* --------------------------------------------------------------------- */
    private ApiResponse<?> buildApiResponse(ErrorCode errorCode) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return apiResponse;
    }
    /* --------------------------------------------------------------------- */

}
