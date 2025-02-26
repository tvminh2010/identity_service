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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import zve.com.vn.dto.request.PermissionRequest;
import zve.com.vn.dto.response.ApiResponse;
import zve.com.vn.dto.response.PermissionResponse;
import zve.com.vn.service.PermissionService;

@RestController
@RequestMapping("/permissions")
public class PermissionController {
  /* ------------------------------------------------------------------ */
  @Autowired private PermissionService permissionService;

  /* ------------------------------------------------------------------ */
  @PostMapping
  public ApiResponse<PermissionResponse> createPermission(@RequestBody PermissionRequest request) {
    ApiResponse<PermissionResponse> apiResponse = new ApiResponse<>();
    apiResponse.setResult(permissionService.createPermission(request));

    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @GetMapping
  public ApiResponse<List<PermissionResponse>> getPermissionses() {
    ApiResponse<List<PermissionResponse>> apiResponse = new ApiResponse<>();
    apiResponse.setResult(permissionService.getAllPermissions());
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */

  @GetMapping("/{permissionId}")
  public ApiResponse<PermissionResponse> getPermissionById(
      @PathVariable("permissionId") String permissionId) {
    ApiResponse<PermissionResponse> apiResponse = new ApiResponse<>();
    apiResponse.setResult(permissionService.getPermissionById(permissionId));
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @GetMapping("/search")
  public ApiResponse<List<PermissionResponse>> searchPermission(
      @RequestParam(name = "keyword", required = false, defaultValue = "") String keyword) {

    ApiResponse<List<PermissionResponse>> apiResponse = new ApiResponse<>();
    apiResponse.setResult(permissionService.searchPermissions(keyword));
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @PutMapping("/{permissionId}")
  public ApiResponse<PermissionResponse> updatePermission(
      @PathVariable("permissionId") String permissionId, @RequestBody PermissionRequest req) {
    ApiResponse<PermissionResponse> apiResponse = new ApiResponse<>();
    apiResponse.setResult(permissionService.updatePermission(permissionId, req));
    return apiResponse;
  }

  /* ------------------------------------------------------------------ */
  @DeleteMapping("/{permissionId}")
  public String deletePermissionById(@PathVariable("permissionId") String permissionId) {
    permissionService.deletePermission(permissionId);
    return "Permission: " + permissionId + " have been deleted";
  }
  /* ------------------------------------------------------------------ */
}
