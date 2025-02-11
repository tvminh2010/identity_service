package zve.com.vn.exception;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import zve.com.vn.dto.response.ApiResponse;
import zve.com.vn.enums.ErrorCode;

@ControllerAdvice
public class GlobalExceptionHandler {
	
	 private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);
	/* --------------------------------------------------------------------- */		
	@ExceptionHandler(value=Exception.class)
	public ResponseEntity<?> exceptionHandler(Exception exception) {
		
		logger.error("Unhandled exception occurred: ", exception);
	    return buildErrorResponse(ErrorCode.UN_CATEGORIZE_EXCEPTION);
	}

	/* --------------------------------------------------------------------- */
	@ExceptionHandler(value=CustomAppException.class)								//Extend từ RuntimeException							
	public ResponseEntity<?> runtimeExceptionHandler (CustomAppException exception) {
		 return buildErrorResponse(exception.getErrorCode());
	}
	/* --------------------------------------------------------------------- */
	
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<?> methodArgumentNotValidExceptionHandler(MethodArgumentNotValidException exception) {
		
		ErrorCode errorCode = ErrorCode.INVALID_KEY; 
		
		try {
			errorCode = ErrorCode.valueOf(exception.getFieldError().getDefaultMessage());
		} catch (IllegalArgumentException e) {
			 logger.warn("Invalid error code: {}", exception.getFieldError().getDefaultMessage());
		}
		
		return buildErrorResponse(errorCode);
	}
	/* --------------------------------------------------------------------- */
	//@ExceptionHandler(MethodArgumentNotValidException.class)
	public ResponseEntity<Map<String, String>> methodArgumentNotValidExceptionHandler2(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new HashMap<>();
        exception.getBindingResult().getFieldErrors().forEach(error ->
            errors.put(error.getField(), error.getDefaultMessage()));
        return new ResponseEntity<>(errors, HttpStatus.BAD_REQUEST);
    }
	/* --------------------------------------------------------------------- */
	@ExceptionHandler(value = AccessDeniedException.class)
	public ResponseEntity<?> accessDeniedExceptionHandler (AccessDeniedException exception) {
		return buildErrorResponse(ErrorCode.UN_AUTHORIRED);
	}
    /* --------------------------------------------------------------------- */
    private ResponseEntity<ApiResponse<?>> buildErrorResponse(ErrorCode errorCode) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }
    /* --------------------------------------------------------------------- */
}
