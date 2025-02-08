package zve.com.vn.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import zve.com.vn.enums.ErrorCode;


@Getter
@Setter
@AllArgsConstructor
public class CustomAppException extends RuntimeException{
	private static final long serialVersionUID = 1L;	
	private ErrorCode errorCode;

}
