package zve.com.vn.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import zve.com.vn.dto.mapper.PermissionMapper;
import zve.com.vn.dto.request.PermissionCreationRequest;
import zve.com.vn.dto.response.PermissionResponse;
import zve.com.vn.entity.Permission;
import zve.com.vn.repository.PermissionRepository;


@Service
public class PermissionService {
	
	@Autowired
	private PermissionRepository permissionRepository;
	@Autowired
	private PermissionMapper permissionMapper;
	
	/* ------------------------------------------------------------------ */
	public PermissionResponse createPermission(PermissionCreationRequest request) {
		Permission permission = permissionMapper.toPermission(request);
		return permissionMapper.toPermissionResponse(permissionRepository.save(permission));
	}
	/* ------------------------------------------------------------------ */
	public
	List<PermissionResponse> getAllPermissions() {
		List<PermissionResponse> result =  new ArrayList<PermissionResponse>();
		Sort sort = Sort.by(Sort.Direction.ASC, "name");
		for(Permission permission: permissionRepository.findAll(sort)) {
			result.add(permissionMapper.toPermissionResponse(permission));
		}
		return result;
	}
	/* ------------------------------------------------------------------ */
	public PermissionResponse getPermissionById(String permission) {
		return permissionMapper.toPermissionResponse(permissionRepository.findById(permission).orElseThrow(() -> new RuntimeException("User ko tồn tại")))  ;
	}
	/* ------------------------------------------------------------------ */
	public void deletePermission(String permission) {
		permissionRepository.deleteById(permission);
	}
	/* ------------------------------------------------------------------ */
	public List<PermissionResponse> searchPermissions(String keyword) {
		List<PermissionResponse> result = new ArrayList<>();

		for (Permission permission: permissionRepository.findAll()) {
			if(permission.getName() != null && 
				permission.getDescription().contains(keyword) || 
				permission.getName().contains(keyword)) {
				result.add(permissionMapper.toPermissionResponse(permission));
			}
		}
		return result;
	}
	/* ------------------------------------------------------------------ */
	public Permission updatePermission (String permissionId, PermissionCreationRequest request) {
		Permission permission = permissionRepository
				.findById(permissionId)
				.orElseThrow(() -> new RuntimeException("User ko tồn tại"));
		return permissionRepository.save(permission);
	}
	/* ------------------------------------------------------------------ */
}
