package zve.com.vn.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import zve.com.vn.dto.request.RoleRequest;
import zve.com.vn.dto.response.ApiResponse;
import zve.com.vn.dto.response.RoleResponse;
import zve.com.vn.service.RoleService;

@RestController
@RequestMapping("/roles")
public class RoleController {
  /* ------------------------------------------------------------------ */
  @Autowired private RoleService roleService;

  /* ------------------------------------------------------------------ */
  @PostMapping
  public ApiResponse<RoleResponse> createRole(@RequestBody RoleRequest request) {
    ApiResponse<RoleResponse> apiResponse = new ApiResponse<>();
    apiResponse.setResult(roleService.createRole(request));
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @GetMapping
  public ApiResponse<List<RoleResponse>> getRoles() {
    ApiResponse<List<RoleResponse>> apiResponse = new ApiResponse<>();
    apiResponse.setResult(roleService.getAllRoles());
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @GetMapping("/{roleId}")
  public ApiResponse<RoleResponse> getRoleById(@PathVariable("roleId") String roleId) {
    ApiResponse<RoleResponse> apiResponse = new ApiResponse<>();
    apiResponse.setResult(roleService.getRoleById(roleId));
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @PutMapping("/{roleId}")
  public ApiResponse<RoleResponse> updatePermission(
      @PathVariable("roleId") String roleId, @RequestBody RoleRequest req) {
    ApiResponse<RoleResponse> apiResponse = new ApiResponse<>();
    apiResponse.setResult(roleService.updateRole(roleId, req));
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @DeleteMapping("/{roleId}")
  public String deletePermissionById(@PathVariable("roleId") String roleId) {
    roleService.deleteRole(roleId);
    return "Role: " + roleId + " have been deleted";
  }
  /* ------------------------------------------------------------------ */
}
