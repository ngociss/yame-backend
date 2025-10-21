package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
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
import org.springframework.web.bind.annotation.*;
import vn.yame.common.enums.UserStatus;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.reponse.UserResponse;
import vn.yame.dto.request.UserCreateRequest;
import vn.yame.dto.request.UserUpdateRequest;
import vn.yame.service.UserService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "User Management", description = "APIs for managing users")
public class UserController {

    private final UserService userService;

    @GetMapping
    @Operation(summary = "Get all users", description = "Retrieve a list of all users")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Users retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<UserResponse>>> getAllUsers() {
        log.info("REST request to get all users");
        List<UserResponse> users = userService.fetchAllUsers();
        return ResponseEntity.ok(ResponseData.success(
                HttpStatus.OK.value(),
                true,
                "Fetched all users successfully",
                users
        ));
    }

    @GetMapping("/page")
    @Operation(summary = "Get users with pagination", description = "Retrieve users with pagination and sorting support")
    public ResponseEntity<ResponseData<Page<UserResponse>>> getUsersWithPagination(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("REST request to get users - page: {}, size: {}, sortBy: {}, direction: {}",
                 page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<UserResponse> users = userService.fetchAllUsersWithPagination(pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Users retrieved successfully",
            users
        ));
    }

    @GetMapping("/search")
    @Operation(summary = "Search users", description = "Search users by name or email")
    public ResponseEntity<ResponseData<Page<UserResponse>>> searchUsers(
            @Parameter(description = "Search keyword") @RequestParam String keyword,
            @Parameter(description = "Page number") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size) {

        log.info("REST request to search users with keyword: {}", keyword);

        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponse> users = userService.searchUsers(keyword, pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Search completed successfully",
            users
        ));
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get users by status", description = "Retrieve users filtered by status")
    public ResponseEntity<ResponseData<List<UserResponse>>> getUsersByStatus(
            @Parameter(description = "User status") @PathVariable UserStatus status) {

        log.info("REST request to get users with status: {}", status);
        List<UserResponse> users = userService.fetchUsersByStatus(status);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Users retrieved successfully",
            users
        ));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get user by ID", description = "Retrieve a user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User found"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<UserResponse>> getUserById(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {

        log.info("REST request to get user with id: {}", id);
        UserResponse user = userService.fetchUserById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Fetched user successfully",
            user
        ));
    }

    @GetMapping("/email/{email}")
    @Operation(summary = "Get user by email", description = "Retrieve a user by their email address")
    public ResponseEntity<ResponseData<UserResponse>> getUserByEmail(
            @Parameter(description = "User email", required = true) @PathVariable String email) {

        log.info("REST request to get user with email: {}", email);
        UserResponse user = userService.fetchUserByEmail(email);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "User retrieved successfully",
            user
        ));
    }

    @PostMapping
    @Operation(summary = "Create a new user", description = "Create a new user with the provided details")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "User created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or email already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<UserResponse>> createUser(
            @RequestBody @Valid UserCreateRequest userCreateRequest) {

        log.info("REST request to create user with email: {}", userCreateRequest.getEmail());
        UserResponse newUser = userService.createUser(userCreateRequest);

        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ResponseData.success(
                    HttpStatus.CREATED.value(),
                    true,
                    "User created successfully",
                    newUser
                ));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update user by ID", description = "Update an existing user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<UserResponse>> updateUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @RequestBody @Valid UserUpdateRequest userUpdateRequest) {

        log.info("REST request to update user with id: {}", id);
        UserResponse updatedUser = userService.updateUser(id, userUpdateRequest);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "User updated successfully",
            updatedUser
        ));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update user status", description = "Update the status of a user (ACTIVE/INACTIVE/SUSPENDED/DELETED)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User status updated successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public ResponseEntity<ResponseData<UserResponse>> updateUserStatus(
            @Parameter(description = "User ID", required = true) @PathVariable Long id,
            @Parameter(description = "New status") @RequestParam UserStatus status) {

        log.info("REST request to update status for user id: {} to {}", id, status);
        UserResponse updatedUser = userService.updateStatus(id, status);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "User status updated successfully",
            updatedUser
        ));
    }

    @PatchMapping("/{id}/verify")
    @Operation(summary = "Verify user", description = "Mark a user as verified and activate their account")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User verified successfully"),
        @ApiResponse(responseCode = "404", description = "User not found")
    })
    public ResponseEntity<ResponseData<UserResponse>> verifyUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {

        log.info("REST request to verify user with id: {}", id);
        UserResponse verifiedUser = userService.verifyUser(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "User verified successfully",
            verifiedUser
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Soft delete user by ID", description = "Soft delete an existing user by their ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "User deleted successfully"),
        @ApiResponse(responseCode = "404", description = "User not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> softDeleteUser(
            @Parameter(description = "User ID", required = true) @PathVariable Long id) {

        log.info("REST request to soft delete user with id: {}", id);
        userService.softDeleteUser(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "User deleted successfully",
            null
        ));
    }
}
