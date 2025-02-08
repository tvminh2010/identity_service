package zve.com.vn.dto.mapper;

import org.mapstruct.Mapper;

import zve.com.vn.dto.request.UserCreationRequest;
import zve.com.vn.dto.response.UserResponse;
import zve.com.vn.entity.User;

@Mapper(componentModel = "spring")
public interface UserMapper {
	User toUser(UserCreationRequest request);
	UserResponse toUserResponse(User user);
}
