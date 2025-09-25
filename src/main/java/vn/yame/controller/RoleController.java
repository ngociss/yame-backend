package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.reponse.RoleResponse;
import vn.yame.dto.request.RoleRequest;
import vn.yame.service.RoleService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class RoleController {
    private final RoleService roleService;

    @PostMapping("/roles")
    @Operation(summary = "Create a new role", description = "Create a new role with the provided details")
    public ResponseEntity<ResponseData<RoleResponse>> createRole(@RequestBody @Valid RoleRequest roleRequest) {
        RoleResponse roleResponse = roleService.createRole(roleRequest);
        return ResponseEntity.ok(ResponseData.
                success(HttpStatus.CREATED.value(), true, "Role created successfully", roleResponse));
    }

    @GetMapping("/roles")
    @Operation(summary = "Get all roles", description = "Retrieve a list of all roles")
    public ResponseEntity<ResponseData<List<RoleResponse>>> getAllRoles() {
        List<RoleResponse> roles = roleService.findAllRoles();
        return ResponseEntity.ok(ResponseData.
                success(HttpStatus.OK.value(), true, "Roles retrieved successfully", roles));

    }

    @GetMapping("/roles/{id}")
    @Operation(summary = "Get role by ID", description = "Retrieve a role by its ID")
    public ResponseEntity<ResponseData<RoleResponse>> getRoleById(@PathVariable Long id) {
        RoleResponse roleResponse = roleService.findRoleById(id);
        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(), true, "Role retrieved successfully", roleResponse));
    }
}
