package zve.com.vn.enums;

import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;

import lombok.Getter;

@Getter
public enum ErrorCode {
  USER_CREATE_SUCCESS(1001, "Tạo user thành công", HttpStatus.OK),
  USER_AUTHENTICATED(1002, "User authenticated", HttpStatus.ACCEPTED),
  USER_DELETE_SUCCESS(1003, "Xóa thành công user", HttpStatus.ALREADY_REPORTED),
  USER_EXISTED(1004, "User đã tồn tại", HttpStatus.BAD_REQUEST),
  EMAIL_EXISTED(1005, "Email đã tồn tại", HttpStatus.BAD_REQUEST),
  INVALID_USERNAME(1006, "Username phải >= {min} ký tự", HttpStatus.BAD_REQUEST),
  INVALID_EMAIL(1007, "Email không đúng định dạng", HttpStatus.BAD_REQUEST),
  INVALID_PASSWORD(1008, "Mật khẩu phải đủ {min} ký tự", HttpStatus.BAD_REQUEST),
  INVALID_DOB1(1008, "Your age must be at least {min}", HttpStatus.BAD_REQUEST),
  INVALID_DOB(1008, "Your ages must be >= {min} years old!", HttpStatus.BAD_REQUEST),
  INVALID_KEY(1009, "Invalid Key", HttpStatus.BAD_REQUEST),
  /* Authentication */
  USER_NOT_EXISTED(1010, "User not existed", HttpStatus.NOT_FOUND),
  PERMISSION_NOTFOUND(1011, "Permission not found!", HttpStatus.NOT_FOUND),
  ROLE_NOT_FOUND(1012, "Role not found!", HttpStatus.NOT_FOUND),
  USER_NOT_FOUND(1013, "User not found!", HttpStatus.NOT_FOUND),
  UN_AUTHENTICATED(1014, "User unauthenticated", HttpStatus.UNAUTHORIZED),
  INVALID_TOKEN(1015, "Token is invalid", HttpStatus.UNAUTHORIZED),
  TOKEN_LOGGED_OUT(1015, "Token already logout", HttpStatus.UNAUTHORIZED),
  TOKEN_EXPIRE(1015, "Token is being expired", HttpStatus.UNAUTHORIZED),
  TOKEN_COULDNOT_GENERATE(1016, "Token could not be generated", HttpStatus.BAD_REQUEST),

  UN_AUTHORIRED(1016, "You do not have permission to access", HttpStatus.FORBIDDEN),
  UN_CATEGORIZE_EXCEPTION(9999, "Uncategorize Exception", HttpStatus.INTERNAL_SERVER_ERROR),
  ;

  /* private fields  */
  private int code;
  private String message;
  private HttpStatusCode httpStatusCode;

  private ErrorCode(int code, String message, HttpStatusCode httpStatusCode) {
    this.code = code;
    this.message = message;
    this.httpStatusCode = httpStatusCode;
  }
}
