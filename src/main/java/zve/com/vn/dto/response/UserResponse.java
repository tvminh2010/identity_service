package zve.com.vn.dto.response;

import java.time.LocalDate;
import java.util.Set;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import zve.com.vn.entity.Role;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserResponse {
	
	String id;
	String username;
	String email;
	String firstName;
	String lastName;
	LocalDate dob;
	Set<Role> roles;
}
