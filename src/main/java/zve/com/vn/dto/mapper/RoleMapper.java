package zve.com.vn.dto.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import zve.com.vn.dto.request.RoleRequest;
import zve.com.vn.dto.response.RoleResponse;
import zve.com.vn.entity.Role;

@Mapper(componentModel = "spring")
public interface RoleMapper {

  @Mapping(target = "permissions", ignore = true)
  Role toRole(RoleRequest request);

  RoleResponse toRoleResponse(Role role);
}
