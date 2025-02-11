package zve.com.vn.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;
import zve.com.vn.validator.DobConstraint;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserRequest {
	
	
	
	@Email(message="INVALID_EMAIL")
	private String email;
	
	@Size(min=3, message="INVALID_USERNAME")
	String username;
	
	@Size(min=8, message = "INVALID_PASSWORD")
	String password;
	String firstName;
	String lastName;
	
	@DobConstraint(min=18, message = "INVALID_DOB")
	LocalDate dob;
	
	List<String> roles;
}
