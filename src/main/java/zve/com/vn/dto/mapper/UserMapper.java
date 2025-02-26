package zve.com.vn.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

import zve.com.vn.dto.request.UserRequest;
import zve.com.vn.dto.request.UserUpdateRequest;
import zve.com.vn.dto.response.UserResponse;
import zve.com.vn.entity.User;

@Mapper(componentModel = "spring")
// @Mapper(componentModel = "spring", nullValuePropertyMappingStrategy =
// NullValuePropertyMappingStrategy.IGNORE)
public interface UserMapper {

  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "id", ignore = true)
  User toUser(UserRequest request);

  UserResponse toUserResponse(User user);

  @Mapping(target = "id", ignore = true)
  @Mapping(target = "roles", ignore = true)
  @Mapping(target = "username", ignore = true)
  void updateUser(@MappingTarget User user, UserUpdateRequest userUpdateRequest);
}
