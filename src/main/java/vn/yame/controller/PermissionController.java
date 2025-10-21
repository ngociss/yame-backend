package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.yame.dto.reponse.PermissionResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.PermissionRequest;
import vn.yame.service.PermissionService;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/permissions")
@Validated
@Slf4j
@Tag(name = "Permission Management", description = "APIs for managing permissions")
@RequiredArgsConstructor
public class PermissionController {

    private final PermissionService permissionService;

    @PostMapping
    @Operation(summary = "Create new permission", description = "Create a new permission with specified details")
    public ResponseEntity<ResponseData<PermissionResponse>> createPermission(
            @Valid @RequestBody PermissionRequest request) {
        log.info("Creating permission with code: {}", request.getCode());

        PermissionResponse permission = permissionService.create(request);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.success(
                        HttpStatus.CREATED.value(),
                        true,
                        "Permission created successfully",
                        permission
                ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update permission", description = "Update an existing permission by ID")
    public ResponseEntity<ResponseData<PermissionResponse>> updatePermission(
            @Parameter(description = "Permission ID") @PathVariable Long id,
            @Valid @RequestBody PermissionRequest request) {
        log.info("Updating permission with id: {}", id);

        PermissionResponse permission = permissionService.update(id, request);

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permission updated successfully",
                permission
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get permission by ID", description = "Retrieve a permission by its ID")
    public ResponseEntity<ResponseData<PermissionResponse>> getPermissionById(
            @Parameter(description = "Permission ID") @PathVariable Long id) {
        log.info("Fetching permission with id: {}", id);

        PermissionResponse permission = permissionService.findById(id);

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permission retrieved successfully",
                permission
        ));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get permission by code", description = "Retrieve a permission by its code")
    public ResponseEntity<ResponseData<PermissionResponse>> getPermissionByCode(
            @Parameter(description = "Permission code") @PathVariable String code) {
        log.info("Fetching permission with code: {}", code);

        PermissionResponse permission = permissionService.findByCode(code);

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permission retrieved successfully",
                permission
        ));
    }

    @GetMapping
    @Operation(summary = "Get all permissions", description = "Retrieve all permissions with pagination and optional search")
    public ResponseEntity<ResponseData<Page<PermissionResponse>>> getAllPermissions(
            @Parameter(description = "Page number (0-based)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "20") int size,
            @Parameter(description = "Sort field") @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "asc") String sortDir,
            @Parameter(description = "Search by name") @RequestParam(required = false) String name) {

        Sort sort = sortDir.equalsIgnoreCase("desc") ?
            Sort.by(sortBy).descending() : Sort.by(sortBy).ascending();
        Pageable pageable = PageRequest.of(page, size, sort);

        Page<PermissionResponse> permissions;
        if (name != null && !name.trim().isEmpty()) {
            log.info("Searching permissions by name: {} with pagination", name);
            permissions = permissionService.searchByName(name.trim(), pageable);
        } else {
            log.info("Fetching all permissions with pagination");
            permissions = permissionService.findAll(pageable);
        }

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permissions retrieved successfully",
                permissions
        ));
    }

    @GetMapping("/resource/{resourceId}")
    @Operation(summary = "Get permissions by resource", description = "Retrieve all permissions for a specific resource")
    public ResponseEntity<ResponseData<List<PermissionResponse>>> getPermissionsByResource(
            @Parameter(description = "Resource ID") @PathVariable Long resourceId) {
        log.info("Fetching permissions for resource id: {}", resourceId);

        List<PermissionResponse> permissions = permissionService.findByResourceId(resourceId);

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permissions retrieved successfully",
                permissions
        ));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active permissions", description = "Retrieve all active permissions")
    public ResponseEntity<ResponseData<List<PermissionResponse>>> getActivePermissions() {
        log.info("Fetching all active permissions");

        List<PermissionResponse> permissions = permissionService.findActivePermissions();

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Active permissions retrieved successfully",
                permissions
        ));
    }



    @PatchMapping("/{id}/status")
    @Operation(summary = "Update permission status", description = "Update the status of a permission (ACTIVE/INACTIVE)")
    public ResponseEntity<ResponseData<PermissionResponse>> updatePermissionStatus(
            @Parameter(description = "Permission ID") @PathVariable Long id,
            @Valid @RequestBody vn.yame.dto.request.UpdateStatusRequest request) {
        log.info("Updating status for permission id: {} to {}", id, request.getStatus());

        PermissionResponse permission = permissionService.updateStatus(id, request.getStatus());

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permission status updated successfully",
                permission
        ));
    }

    @PatchMapping("/{id}/verify")
    @Operation(summary = "Verify permission", description = "Mark a permission as verified")
    public ResponseEntity<ResponseData<PermissionResponse>> verifyPermission(
            @Parameter(description = "Permission ID") @PathVariable Long id) {
        log.info("Verifying permission id: {}", id);

        PermissionResponse permission = permissionService.verifyPermission(id);

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permission verified successfully",
                permission
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete permission", description = "Delete a permission by ID")
    public ResponseEntity<ResponseData<String>> deletePermission(
            @Parameter(description = "Permission ID") @PathVariable Long id) {
        log.info("Deleting permission with id: {}", id);

        permissionService.delete(id);

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permission deleted successfully",
                null
        ));
    }

    @GetMapping("/exists/code/{code}")
    @Operation(summary = "Check if permission code exists", description = "Check if a permission with given code exists")
    public ResponseEntity<ResponseData<Boolean>> checkPermissionCodeExists(
            @Parameter(description = "Permission code") @PathVariable String code) {
        boolean exists = permissionService.existsByCode(code);

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permission code existence checked",
                exists
        ));
    }

    @GetMapping("/exists/name/{name}")
    @Operation(summary = "Check if permission name exists", description = "Check if a permission with given name exists")
    public ResponseEntity<ResponseData<Boolean>> checkPermissionNameExists(
            @Parameter(description = "Permission name") @PathVariable String name) {
        boolean exists = permissionService.existsByName(name);

        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Permission name existence checked",
                exists
        ));
    }
}
