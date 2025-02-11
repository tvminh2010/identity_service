package zve.com.vn.service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import zve.com.vn.dto.mapper.RoleMapper;
import zve.com.vn.dto.request.RoleRequest;
import zve.com.vn.dto.response.RoleResponse;
import zve.com.vn.entity.Permission;
import zve.com.vn.entity.Role;
import zve.com.vn.enums.ErrorCode;
import zve.com.vn.exception.CustomAppException;
import zve.com.vn.repository.PermissionRepository;
import zve.com.vn.repository.RoleRepository;

@Service
public class RoleService {
	
	@Autowired
	RoleRepository roleRepository;
	
	@Autowired
	PermissionRepository permissionRepository;
	
	@Autowired
	RoleMapper roleMapper;
	
	/* ------------------------------------------------------------------ */
	public RoleResponse createRole(RoleRequest request) {
		Role role = roleMapper.toRole(request);
		List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
		role.setPermissions(new HashSet<>(permissions));
		return roleMapper.toRoleResponse(roleRepository.save(role));
	}
	/* ------------------------------------------------------------------ */
	public List<RoleResponse> getAllRoles() {
		//List<RoleResponse> result =  new ArrayList<RoleResponse>();
		Sort sort = Sort.by(Sort.Direction.ASC, "name");
		/*
		for(Role role: roleRepository.findAll(sort)) {
			result.add(roleMapper.toRoleResponse(role));
		}*/
		//return result;
		return roleRepository.findAll(sort)
				.stream()
				.map(roleMapper::toRoleResponse)
				.toList();
		
	}
	/* ------------------------------------------------------------------ */
	public RoleResponse getRoleById(String roleId) {
		return roleMapper.toRoleResponse(roleRepository.findById(roleId)
				.orElseThrow(() -> new RuntimeException("Role ko tồn tại")))  ;
	}
	/* ------------------------------------------------------------------ */
	public void deleteRole(String roleId) {
		Optional<Role> optRole = roleRepository.findById(roleId);
		if(!optRole.isPresent()) {
			throw new CustomAppException(ErrorCode.ROLE_NOTFOUND);
		} else {
			roleRepository.deleteById(roleId);
		}
	}
	/* ------------------------------------------------------------------ */
	public RoleResponse updateRole (String roleId, RoleRequest request) {
		Optional<Role> optRole = roleRepository.findById(roleId);
		if(!optRole.isPresent()) {
			throw new CustomAppException(ErrorCode.ROLE_NOTFOUND);
		} else {
			Role role = roleMapper.toRole(request);
			List<Permission> permissions = permissionRepository.findAllById(request.getPermissions());
			role.setPermissions(new HashSet<>(permissions));
			
			return roleMapper.toRoleResponse(roleRepository.save(role));
		}
	}
	/* ------------------------------------------------------------------ */
}
