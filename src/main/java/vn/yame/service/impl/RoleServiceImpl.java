package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.reponse.RoleResponse;
import vn.yame.dto.request.RoleRequest;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.InvalidDataException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.RoleMapper;
import vn.yame.model.Role;
import vn.yame.repository.RoleRepository;
import vn.yame.service.RoleService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {
        log.info("Creating new role with name: {}", roleRequest.getName());

        if (roleRepository.existsByName(roleRequest.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.ROLE_ALREADY_EXISTS,
                "Role with name '" + roleRequest.getName() + "' already exists"
            );
        }

        Role role = roleMapper.toEntity(roleRequest);
        role.setStatus(CommonStatus.INACTIVE);
        Role savedRole = roleRepository.save(role);

        log.info("Role created successfully with id: {}", savedRole.getId());
        return roleMapper.toResponse(savedRole);
    }

    @Override
    @Transactional(readOnly = true)
    public List<RoleResponse> findAllRoles() {
        log.info("Fetching all roles");
        List<Role> roles = roleRepository.findAll();
        return roles.stream().map(roleMapper::toResponse).toList();
    }

    @Override
    @Transactional(readOnly = true)
    public RoleResponse findRoleById(Long id) {
        log.info("Fetching role with id: {}", id);

        Role role = roleRepository.findRoleById(id)
                .orElseThrow(() -> new NotFoundResourcesException(
                    ErrorCode.ROLE_NOT_FOUND,
                    "Role not found with id: " + id
                ));

        return roleMapper.toResponse(role);
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest roleRequest) {
        log.info("Updating role with id: {}", id);

        Role role = roleRepository.findRoleById(id)
                .orElseThrow(() -> new NotFoundResourcesException(
                    ErrorCode.ROLE_NOT_FOUND,
                    "Role not found with id: " + id
                ));

        // Check if new name already exists (if name is being changed)
        if (!role.getName().equals(roleRequest.getName()) &&
            roleRepository.existsByName(roleRequest.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.ROLE_ALREADY_EXISTS,
                "Role with name '" + roleRequest.getName() + "' already exists"
            );
        }

        roleMapper.updateEntity(role, roleRequest);
        Role updatedRole = roleRepository.save(role);

        log.info("Role updated successfully with id: {}", id);
        return roleMapper.toResponse(updatedRole);
    }

    @Override
    public void deleteRole(Long id) {
        log.info("Deleting role with id: {}", id);

        Role role = roleRepository.findRoleById(id)
                .orElseThrow(() -> new NotFoundResourcesException(
                    ErrorCode.ROLE_NOT_FOUND,
                    "Role not found with id: " + id
                ));

        // Check if role status is INACTIVE
        if (role.getStatus() != CommonStatus.INACTIVE) {
            throw new InvalidDataException(
                ErrorCode.ROLE_ALREADY_EXISTS,
                "Cannot delete role with ACTIVE status. Please set role to INACTIVE first."
            );
        }

        // Check if role has users
        if (role.getUsers() != null && !role.getUsers().isEmpty()) {
            throw new ExistingResourcesException(
                ErrorCode.ROLE_ALREADY_EXISTS,
                "Cannot delete role with existing users. Please reassign users first."
            );
        }

        roleRepository.delete(role);
        log.info("Role deleted successfully with id: {}", id);
    }
}
