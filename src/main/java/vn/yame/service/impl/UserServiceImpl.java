package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.UserStatus;
import vn.yame.dto.reponse.UserResponse;
import vn.yame.dto.request.UserCreateRequest;
import vn.yame.dto.request.UserUpdateRequest;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.UserMapper;
import vn.yame.model.Role;
import vn.yame.model.User;
import vn.yame.repository.RoleRepository;
import vn.yame.repository.UserRepository;
import vn.yame.service.UserService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


@Service
@Slf4j(topic = "USERSERVICE")
@RequiredArgsConstructor
@Transactional
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final @Lazy PasswordEncoder passwordEncoder;


    @Override
    public UserDetailsService userDetailsService() {
        return email -> {
            log.info(">>> Đang login với email: [{}]", email);
            return userRepository.findByEmail(email)
                    .orElseThrow(() -> new NotFoundResourcesException("User not found with email: " + email));
        };
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> fetchAllUsers() {
        log.info("Fetching all users");
        List<User> users = userRepository.findAll();
        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> fetchAllUsersWithPagination(Pageable pageable) {
        log.info("Fetching users with pagination - page: {}, size: {}",
                 pageable.getPageNumber(), pageable.getPageSize());
        Page<User> users = userRepository.findAll(pageable);
        return users.map(userMapper::toResponse);
    }

    @Override
    public UserResponse createUser(UserCreateRequest req) {
        log.info("Creating new user with email: {}", req.getEmail());

        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ExistingResourcesException("Email already exists: " + req.getEmail());
        }

        // Find roles by IDs
        Set<Role> roles = req.getRoleIds().stream()
                .map(roleId -> roleRepository.findById(roleId)
                        .orElseThrow(() -> new NotFoundResourcesException("Role not found with id: " + roleId)))
                .collect(Collectors.toSet());

        User user = userMapper.toEntity(req);
        user.setRoles(roles);
        user.setStatus(UserStatus.INACTIVE); // Changed from ACTIVE to INACTIVE
        user.setVerified(false);
        user.setPassword(passwordEncoder.encode(user.getPassword()));

        User savedUser = userRepository.save(user);
        log.info("User created successfully with id: {}", savedUser.getId());

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse fetchUserById(Long id) {
        log.info("Fetching user with id: {}", id);
        User user = findUserEntityById(id);
        return userMapper.toResponse(user);
    }

    @Override
    @Transactional(readOnly = true)
    public UserResponse fetchUserByEmail(String email) {
        log.info("Fetching user with email: {}", email);
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new NotFoundResourcesException("User not found with email: " + email));
        return userMapper.toResponse(user);
    }

    private User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("User not found with id: " + id));
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest req) {
        log.info("Updating user with id: {}", id);

        User user = findUserEntityById(id);

        // Update roles if provided
        if (req.getRoleIds() != null && !req.getRoleIds().isEmpty()) {
            Set<Role> roles = req.getRoleIds().stream()
                    .map(roleId -> roleRepository.findById(roleId)
                            .orElseThrow(() -> new NotFoundResourcesException("Role not found with id: " + roleId)))
                    .collect(Collectors.toSet());
            user.setRoles(roles);
        }

        userMapper.updateUserFromDto(req, user);

        // Only encode password if it's being updated and not empty
        if (req.getPassword() != null && !req.getPassword().isEmpty()) {
            user.setPassword(passwordEncoder.encode(req.getPassword()));
        }

        User savedUser = userRepository.save(user);
        log.info("User updated successfully with id: {}", id);

        return userMapper.toResponse(savedUser);
    }

    @Override
    public void softDeleteUser(Long id) {
        log.info("Soft deleting user with id: {}", id);

        User user = findUserEntityById(id);
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);

        log.info("User soft deleted successfully with id: {}", id);
    }

    @Override
    public UserResponse updateStatus(Long id, UserStatus status) {
        log.info("Updating status for user id: {} to {}", id, status);

        User user = findUserEntityById(id);
        user.setStatus(status);
        User savedUser = userRepository.save(user);

        log.info("User status updated successfully for id: {}", id);
        return userMapper.toResponse(savedUser);
    }

    @Override
    public UserResponse verifyUser(Long id) {
        log.info("Verifying user with id: {}", id);

        User user = findUserEntityById(id);
        user.setVerified(true);

        // Optionally activate user when verified
        if (user.getStatus() == UserStatus.INACTIVE) {
            user.setStatus(UserStatus.ACTIVE);
        }

        User savedUser = userRepository.save(user);
        log.info("User verified successfully for id: {}", id);

        return userMapper.toResponse(savedUser);
    }

    @Override
    @Transactional(readOnly = true)
    public List<UserResponse> fetchUsersByStatus(UserStatus status) {
        log.info("Fetching users with status: {}", status);

        List<User> users = userRepository.findAll().stream()
                .filter(user -> user.getStatus() == status)
                .toList();

        return users.stream()
                .map(userMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<UserResponse> searchUsers(String keyword, Pageable pageable) {
        log.info("Searching users with keyword: {}", keyword);

        Page<User> users = userRepository.findByFullNameContainingIgnoreCaseOrEmailContainingIgnoreCase(
                keyword, keyword, pageable);

        return users.map(userMapper::toResponse);
    }
}
