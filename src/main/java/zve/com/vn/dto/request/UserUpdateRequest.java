package zve.com.vn.dto.request;

import java.time.LocalDate;

import org.springframework.stereotype.Component;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;

@Data
@Component
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserUpdateRequest {

	@Size(min=3, message="username phải ít nhất 3 ký tự")
	private String username;
	
	@Email(message="Không đúng định dạng email")
	private String email;
	
	@Size(min=8, message = "Mật khẩu phải đủ 8 ký tự")
	String password;
	String firstName;
	String lastName;
	LocalDate dob;
}
