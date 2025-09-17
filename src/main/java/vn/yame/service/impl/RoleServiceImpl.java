package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.yame.dto.reponse.RoleResponse;
import vn.yame.dto.request.RoleRequest;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.mapper.RoleMapper;
import vn.yame.model.Role;
import vn.yame.repository.RoleRepository;
import vn.yame.service.RoleService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RoleMapper roleMapper;

    @Override
    public RoleResponse createRole(RoleRequest roleRequest) {
        if (roleRepository.existsByName(roleRequest.getName())) {
            throw new ExistingResourcesException("Role with name " + roleRequest.getName() + " already exists");
        }
        Role role = roleMapper.toEntity(roleRequest);
        Role savedRole = roleRepository.save(role);
        return roleMapper.toResponse(savedRole);
    }

    @Override
    public List<RoleResponse> findAllRoles() {
        return List.of();
    }

    @Override
    public RoleResponse findRoleById(Long id) {
        return null;
    }

    @Override
    public RoleResponse updateRole(Long id, RoleRequest roleRequest) {
        return null;
    }

    @Override
    public void deleteRole(Long id) {

    }
}
