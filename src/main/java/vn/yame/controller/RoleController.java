package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.reponse.RoleResponse;
import vn.yame.dto.request.RoleRequest;
import vn.yame.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/roles")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Role Management", description = "APIs for managing user roles and permissions")
public class RoleController {
    private final RoleService roleService;

    @PostMapping
    @Operation(summary = "Create a new role", description = "Create a new role with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Role created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or role already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<RoleResponse>> createRole(@RequestBody @Valid RoleRequest roleRequest) {
        log.info("REST request to create role: {}", roleRequest.getName());

        RoleResponse roleResponse = roleService.createRole(roleRequest);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Role created successfully",
                roleResponse
            ));
    }

    @GetMapping
    @Operation(summary = "Get all roles", description = "Retrieve a list of all roles")
    public ResponseEntity<ResponseData<List<RoleResponse>>> getAllRoles() {
        log.info("REST request to get all roles");

        List<RoleResponse> roles = roleService.findAllRoles();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Roles retrieved successfully",
            roles
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieve a role by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role found"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<RoleResponse>> getRoleById(
            @Parameter(description = "Role ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get role with id: {}", id);

        RoleResponse roleResponse = roleService.findRoleById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Role retrieved successfully",
            roleResponse
        ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a role", description = "Update an existing role by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role updated successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input or role name already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<RoleResponse>> updateRole(
            @Parameter(description = "Role ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody RoleRequest roleRequest) {
        log.info("REST request to update role with id: {}", id);

        RoleResponse roleResponse = roleService.updateRole(id, roleRequest);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Role updated successfully",
            roleResponse
        ));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update role status", description = "Update the status of a role (ACTIVE/INACTIVE)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public ResponseEntity<ResponseData<RoleResponse>> updateRoleStatus(
            @Parameter(description = "Role ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody vn.yame.dto.request.UpdateStatusRequest request) {
        log.info("REST request to update status for role id: {} to {}", id, request.getStatus());

        RoleResponse roleResponse = roleService.updateStatus(id, request.getStatus());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Role status updated successfully",
            roleResponse
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a role", description = "Delete a role by ID. Cannot delete if role has users.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Role deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Role not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete role with existing users"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteRole(
            @Parameter(description = "Role ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete role with id: {}", id);

        roleService.deleteRole(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Role deleted successfully",
            null
        ));
    }
}
