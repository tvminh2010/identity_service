package zve.com.vn.dto.request;

import java.time.LocalDate;
import java.util.List;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

	
	@Email(message="INVALID_EMAIL")
	private String email;
	
	@Size(min=8, message = "INVALID_PASSWORD")
	String password;
	
	String firstName;
	String lastName;
	LocalDate dob;
	List<String> roles;
}
