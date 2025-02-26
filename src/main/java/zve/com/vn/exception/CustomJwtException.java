package zve.com.vn.exception;

import org.springframework.security.oauth2.jwt.JwtException;

import lombok.Getter;
import lombok.Setter;
import zve.com.vn.enums.ErrorCode;

@Getter
@Setter
public class CustomJwtException extends JwtException {
  private static final long serialVersionUID = 1L;
  private final ErrorCode errorCode;

  public CustomJwtException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
