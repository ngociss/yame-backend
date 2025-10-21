package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import vn.yame.common.enums.UserStatus;
import vn.yame.dto.reponse.UserResponse;
import vn.yame.dto.request.UserCreateRequest;
import vn.yame.dto.request.UserUpdateRequest;

import java.util.List;

public interface UserService {

    UserDetailsService userDetailsService();

    List<UserResponse> fetchAllUsers();

    Page<UserResponse> fetchAllUsersWithPagination(Pageable pageable);

    UserResponse createUser(UserCreateRequest req);

    UserResponse fetchUserById(Long id);

    UserResponse updateUser(Long id, UserUpdateRequest req);

    void softDeleteUser(Long id);

    UserResponse updateStatus(Long id, UserStatus status);

    UserResponse verifyUser(Long id);

    List<UserResponse> fetchUsersByStatus(UserStatus status);

    Page<UserResponse> searchUsers(String keyword, Pageable pageable);

    UserResponse fetchUserByEmail(String email);
}
