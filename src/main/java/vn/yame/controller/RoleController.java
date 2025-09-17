package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.reponse.RoleResponse;
import vn.yame.dto.request.RoleRequest;
import vn.yame.service.RoleService;

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
}
