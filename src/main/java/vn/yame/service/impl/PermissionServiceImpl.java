package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.dto.reponse.PermissionResponse;
import vn.yame.dto.request.PermissionRequest;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.PermissionMapper;
import vn.yame.model.Permission;
import vn.yame.model.Resource;
import vn.yame.repository.PermissionRepository;
import vn.yame.repository.ResourceRepository;
import vn.yame.service.PermissionService;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class PermissionServiceImpl implements PermissionService {

    private final PermissionRepository permissionRepository;
    private final ResourceRepository resourceRepository;
    private final PermissionMapper permissionMapper;

    @Override
    public PermissionResponse create(PermissionRequest request) {
        log.info("Creating new permission with code: {}", request.getCode());

        // Check if permission code already exists
        if (permissionRepository.existsByCode(request.getCode())) {
            throw new ExistingResourcesException("Permission with code '" + request.getCode() + "' already exists");
        }

        // Check if permission name already exists
        if (permissionRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException("Permission with name '" + request.getName() + "' already exists");
        }

        // Find resource
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + request.getResourceId()));

        // Create permission
        Permission permission = permissionMapper.toEntity(request);
        permission.setResource(resource);
        permission.setVerified(true); // Default verified for new permissions

        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created successfully with id: {}", savedPermission.getId());

        return permissionMapper.toResponse(savedPermission);
    }

    @Override
    public PermissionResponse update(Long id, PermissionRequest request) {
        log.info("Updating permission with id: {}", id);

        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with id: " + id));

        if (!existingPermission.getCode().equals(request.getCode()) &&
            permissionRepository.existsByCode(request.getCode())) {
            throw new ExistingResourcesException("Permission with code '" + request.getCode() + "' already exists");
        }

        if (!existingPermission.getName().equals(request.getName()) &&
            permissionRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException("Permission with name '" + request.getName() + "' already exists");
        }

        if (!existingPermission.getResource().getId().equals(request.getResourceId())) {
            Resource resource = resourceRepository.findById(request.getResourceId())
                    .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + request.getResourceId()));
            existingPermission.setResource(resource);
        }

        permissionMapper.updateEntityFromRequest(existingPermission, request);

        Permission savedPermission = permissionRepository.save(existingPermission);
        log.info("Permission updated successfully with id: {}", savedPermission.getId());

        return permissionMapper.toResponse(savedPermission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse findById(Long id) {
        Permission permission = permissionRepository.findByIdWithResource(id)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with id: " + id));
        return permissionMapper.toResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public PermissionResponse findByCode(String code) {
        Permission permission = permissionRepository.findByCode(code)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with code: " + code));
        return permissionMapper.toResponse(permission);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> findAll(Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findAll(pageable);
        return permissions.map(permissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<PermissionResponse> searchByName(String name, Pageable pageable) {
        Page<Permission> permissions = permissionRepository.findByNameContainingIgnoreCase(name, pageable);
        return permissions.map(permissionMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> findByResourceId(Long resourceId) {
        List<Permission> permissions = permissionRepository.findByResourceId(resourceId);
        return permissions.stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> findActivePermissions() {
        List<Permission> permissions = permissionRepository.findByIsActiveTrue();
        return permissions.stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public List<PermissionResponse> findByCodes(Set<String> codes) {
        List<Permission> permissions = permissionRepository.findByCodes(codes);
        return permissions.stream()
                .map(permissionMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    public void delete(Long id) {
        log.info("Deleting permission with id: {}", id);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with id: " + id));

        // Check if permission is being used by any roles
        if (permission.getRoles() != null && !permission.getRoles().isEmpty()) {
            throw new ExistingResourcesException("Cannot delete permission. It is assigned to " +
                permission.getRoles().size() + " role(s)");
        }

        permissionRepository.delete(permission);
        log.info("Permission deleted successfully with id: {}", id);
    }

    @Override
    public void toggleActive(Long id) {
        log.info("Toggling active status for permission with id: {}", id);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with id: " + id));

        permission.setActive(!permission.isActive());
        permissionRepository.save(permission);

        log.info("Permission active status toggled to: {} for id: {}", permission.isActive(), id);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByCode(String code) {
        return permissionRepository.existsByCode(code);
    }

    @Override
    @Transactional(readOnly = true)
    public boolean existsByName(String name) {
        return permissionRepository.existsByName(name);
    }
}
