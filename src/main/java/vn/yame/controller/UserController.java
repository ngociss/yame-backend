package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.reponse.UserResponse;
import vn.yame.dto.request.UserCreateRequest;
import vn.yame.dto.request.UserUpdateRequest;
import vn.yame.model.User;
import vn.yame.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @GetMapping("/users")
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    public ResponseEntity<ResponseData<List<UserResponse>>> getAllUsers() {
        List<UserResponse> users = userService.fetchAllUsers();
        return ResponseEntity.ok(ResponseData.
                success(HttpStatus.OK.value(), true, "Fetched all users successfully", users));
    }

    @GetMapping("/users/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    public ResponseEntity<ResponseData<UserResponse>> getUserById(@PathVariable Long id) {
        // Assuming userService has a method to fetch user by ID
        UserResponse user = userService.fetchUserById(id);
        return ResponseEntity.ok(ResponseData.
                success(HttpStatus.OK.value(), true, "Fetched user successfully", user));
    }

    @PostMapping("/users")
    @Operation(summary = "Create a new user", description = "Create a new user with the provided details")
    public ResponseEntity<ResponseData<UserResponse>> createUser(@RequestBody @Valid UserCreateRequest userCreateRequest) {
        UserResponse newUser = userService.createUser(userCreateRequest);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.success(HttpStatus.CREATED.value(), true, "User created successfully", newUser));
    }

    @PutMapping("/users/{id}")
    @Operation(summary = "Update user by ID", description = "Update an existing user by their ID")
    public ResponseEntity<ResponseData<UserResponse>> updateUser(@PathVariable Long id, @RequestBody @Valid UserUpdateRequest userUpdateRequest) {
        UserResponse updatedUser = userService.updateUser(id, userUpdateRequest);
        return ResponseEntity.ok(ResponseData.
                success(HttpStatus.OK.value(), true, "User updated successfully", updatedUser));
    }

    @PatchMapping("/users/delete/{id}")
    @Operation(summary = "Soft delete user by ID", description = "Soft delete an existing user by their ID")
    public ResponseEntity<ResponseData<UserResponse>> softDeleteUser(@PathVariable Long id) {
        userService.softDeleteUser(id);
        return ResponseEntity.ok(ResponseData.success(HttpStatus.OK.value(), true, "User deleted successfully", null));
    }


}
