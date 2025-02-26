package zve.com.vn.exception;

import lombok.Getter;
import lombok.Setter;
import zve.com.vn.enums.ErrorCode;

@Getter
@Setter
public class CustomAppException extends RuntimeException {
  private static final long serialVersionUID = 1L;
  private final ErrorCode errorCode;

  public CustomAppException(ErrorCode errorCode) {
    super(errorCode.getMessage());
    this.errorCode = errorCode;
  }
}
