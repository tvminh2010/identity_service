package zve.com.vn.dto.request;

import java.time.LocalDate;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserCreationRequest {
	@Size(min=3, message="INVALID_USERNAME")
	String username;
	
	@Email(message="INVALID_EMAIL")
	private String email;
	
	@Size(min=8, message = "INVALID_PASSWORD")
	String password;
	String firstName;
	String lastName;
	LocalDate dob;
	
}
