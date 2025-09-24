package vn.yame.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import vn.yame.dto.reponse.UserResponse;
import vn.yame.dto.request.UserCreateRequest;
import vn.yame.dto.request.UserUpdateRequest;
import vn.yame.model.User;

import java.util.List;

public interface UserService {

    UserDetailsService userDetailsService();

    public List<UserResponse> fetchAllUsers();

    public UserResponse createUser(UserCreateRequest req);

    public UserResponse fetchUserById(Long id);

    public UserResponse updateUser(Long id, UserUpdateRequest req);

    public void softDeleteUser(Long id);
}
