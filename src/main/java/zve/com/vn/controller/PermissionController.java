package zve.com.vn.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import jakarta.validation.Valid;
import zve.com.vn.dto.request.PermissionCreationRequest;
import zve.com.vn.dto.response.ApiResponse;
import zve.com.vn.dto.response.PermissionResponse;
import zve.com.vn.entity.Permission;
import zve.com.vn.service.PermissionService;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
	/* ------------------------------------------------------------------ */
	@Autowired
	private PermissionService permissionService;
	
	/* ------------------------------------------------------------------ */
	@PostMapping
	public ApiResponse<PermissionResponse> createPermission(@RequestBody @Valid PermissionCreationRequest request) {
		ApiResponse<PermissionResponse> apiResponse = new ApiResponse<PermissionResponse>();
		apiResponse.setResult(permissionService.createPermission(request));
		
		return apiResponse;
	}
	/* ------------------------------------------------------------------ */
	@GetMapping("")
	public List<PermissionResponse> getPermissionses() {
		return permissionService.getAllPermissions();
	}
	/* ------------------------------------------------------------------ */
	
	@GetMapping("/{permissionId}")
	public PermissionResponse getPermissionById(@PathVariable("permissionId") String permissionId) {
		return permissionService.getPermissionById(permissionId);
	}
	/* ------------------------------------------------------------------ */
	@GetMapping("/search")
	public ResponseEntity<?> searchPermission(@RequestParam(name = "keyword", required = false, defaultValue= "") String keyword) {
		List<PermissionResponse> permissions = permissionService.searchPermissions(keyword);
		return ResponseEntity.ok(permissions);
	}
	/* ------------------------------------------------------------------ */
	@PutMapping("/{permissionId}")
	public Permission updatePermission (@PathVariable("permissionId") String permissionId,  @RequestBody PermissionCreationRequest req) {
		return permissionService.updatePermission(permissionId, req);
	}
	/* ------------------------------------------------------------------ */
	@DeleteMapping("/{permissionId}")
	public String deletePermissionById(@PathVariable("permissionId") String permissionId) {
		permissionService.deletePermission(permissionId);
		return "User with id: "+ permissionId + "have been deleted";
	}
	/* ------------------------------------------------------------------ */
}
