package zve.com.vn.exception;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import jakarta.validation.ConstraintViolation;
import lombok.extern.slf4j.Slf4j;
import zve.com.vn.dto.response.ApiResponse;
import zve.com.vn.enums.ErrorCode;

@ControllerAdvice
@Slf4j
public class GlobalExceptionHandler {
	
	 private static final String MIN_ATTRIBUTE = "min";
	 private static final List<String> ATTRIBUTES = List.of("min", "max", "length", "size", "value", "pattern");
	/* --------------------------------------------------------------------- */		
	@ExceptionHandler(value=Exception.class)
	public ResponseEntity<?> handleException (Exception exception) {
		log.error("Unhandled exception occurred: ", exception);
	    return buildErrorResponse(ErrorCode.UN_CATEGORIZE_EXCEPTION);
	}
	/* --------------------------------------------------------------------- */
	@ExceptionHandler(value=CustomAppException.class)														
	public ResponseEntity<?> handleCustomAppException (CustomAppException exception) {
		 return buildErrorResponse(exception.getErrorCode());
	}
	/* --------------------------------------------------------------------- */
	@ExceptionHandler(value = MethodArgumentNotValidException.class)
	public ResponseEntity<?> handleValidationException (MethodArgumentNotValidException exception) {
		var fieldError = exception.getFieldError();
		 if (fieldError == null) {
	            return buildErrorResponse(ErrorCode.INVALID_KEY);
	     }
		 
		String enumKey = exception.getFieldError().getDefaultMessage();
		ErrorCode errorCode = ErrorCode.INVALID_KEY; 
		Map<?, ?> attributes = Collections.emptyMap();

		try {
			errorCode = ErrorCode.valueOf(enumKey);
			var constraintViolation = exception
					.getBindingResult()
					.getAllErrors()
					.get(0)
					.unwrap(ConstraintViolation.class);
			attributes = constraintViolation.getConstraintDescriptor().getAttributes();	
			
			log.info("Min attribute: {}", attributes.get(MIN_ATTRIBUTE));
			
		} catch (IllegalArgumentException e) {
			 log.warn("Invalid error code: {}", enumKey);
		}
	return buildErrorResponseWithAttribute (errorCode, attributes);
	}
	/* --------------------------------------------------------------------- */
	@ExceptionHandler(value = AccessDeniedException.class)
	public ResponseEntity<?> handleAccessDeniedException (AccessDeniedException exception) {
		return buildErrorResponse(ErrorCode.UN_AUTHORIRED);
	}
    /* --------------------------------------------------------------------- */
	/* --------------------------------------------------------------------- */
	/* --------------------------------------------------------------------- */
    private ResponseEntity<ApiResponse<?>> buildErrorResponse(ErrorCode errorCode) {
        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(errorCode.getMessage());
        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }
    /* --------------------------------------------------------------------- */
    private ResponseEntity<ApiResponse<?>> buildErrorResponseWithAttribute(ErrorCode errorCode, Map<?, ?> attributes) {
        String message = errorCode.getMessage();

        if (attributes != null) {
            for (String attribute : ATTRIBUTES) {
                if (attributes.containsKey(attribute)) {
                    Object valueObj = attributes.get(attribute);
                    String value = valueObj != null ? valueObj.toString() : "";
                    message = message.replace("{" + attribute + "}", value);
                }
            }
        }

        ApiResponse<?> apiResponse = new ApiResponse<>();
        apiResponse.setCode(errorCode.getCode());
        apiResponse.setMessage(message);

        return ResponseEntity.status(errorCode.getHttpStatusCode()).body(apiResponse);
    }
    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
    /* --------------------------------------------------------------------- */
}
