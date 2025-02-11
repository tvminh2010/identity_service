package zve.com.vn.dto.mapper;

import org.mapstruct.Mapper;

import zve.com.vn.dto.request.PermissionRequest;
import zve.com.vn.dto.response.PermissionResponse;
import zve.com.vn.entity.Permission;

@Mapper(componentModel = "spring")
public interface PermissionMapper {
	Permission toPermission(PermissionRequest request);
	PermissionResponse toPermissionResponse(Permission permission);
}
