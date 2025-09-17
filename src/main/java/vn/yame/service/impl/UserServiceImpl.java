package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import vn.yame.common.enums.UserStatus;
import vn.yame.dto.reponse.AddressResponse;
import vn.yame.dto.reponse.UserResponse;
import vn.yame.dto.request.UserCreateRequest;
import vn.yame.dto.request.UserUpdateRequest;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.AddressMapper;
import vn.yame.mapper.UserMapper;
import vn.yame.model.Address;
import vn.yame.model.Role;
import vn.yame.model.User;
import vn.yame.repository.AddressRepository;
import vn.yame.repository.RoleRepository;
import vn.yame.repository.UserRepository;
import vn.yame.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j(topic = "USERSERVICE")
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final RoleRepository roleRepository;
    private final AddressMapper addressMapper;


    @Override
    public List<UserResponse> fetchAllUsers() {
        List<User> users = userRepository.findAll();
        List<UserResponse> userResponses = users.stream()
                .map(userMapper::toResponse)
                .toList();
        return userResponses;
    }

    @Override
    public UserResponse createUser(UserCreateRequest req) {
        if (userRepository.existsByEmail(req.getEmail())) {
            throw new ExistingResourcesException("Email already exists");
        }
        Role role = roleRepository.findByName(req.getRoleName());
        User user = userMapper.toEntity(req);
        user.setRole(role);
        user.setStatus(UserStatus.ACTIVE);
        User savedUser = userRepository.save(user);
        UserResponse response = userMapper.toResponse(savedUser);
        return response;
    }

    @Override
    public UserResponse fetchUserById(Long id) {
        User user = findUserEntityById(id);
        UserResponse userResponse = userMapper.toResponse(user);
        return userResponse;
    }

    public User findUserEntityById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("User not found with id: " + id));
    }

    @Override
    public UserResponse updateUser(Long id, UserUpdateRequest req) {
        User user = findUserEntityById(id);
        userMapper.updateUserFromDto(req, user);
        userRepository.save(user);
        UserResponse response = userMapper.toResponse(user);
        return response;
    }

    @Override
    public void softDeleteUser(Long id) {
        User user = findUserEntityById(id);
        user.setStatus(UserStatus.DELETED);
        userRepository.save(user);
    }
}
