package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
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

        // Validate code format (should be uppercase with underscores)
        String code = request.getCode().trim().toUpperCase();

        // Check if permission code already exists
        if (permissionRepository.existsByCode(code)) {
            throw new ExistingResourcesException("Permission with code '" + code + "' already exists");
        }

        // Check if permission name already exists
        if (permissionRepository.existsByName(request.getName().trim())) {
            throw new ExistingResourcesException("Permission with name '" + request.getName() + "' already exists");
        }

        // Find resource and verify it exists
        Resource resource = resourceRepository.findById(request.getResourceId())
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + request.getResourceId()));

        // Create permission
        Permission permission = permissionMapper.toEntity(request);
        permission.setCode(code); // Set normalized code
        permission.setResource(resource);
        permission.setStatus(CommonStatus.ACTIVE);
        permission.setVerified(false); // Changed to false - should be verified by admin

        Permission savedPermission = permissionRepository.save(permission);
        log.info("Permission created successfully with id: {}", savedPermission.getId());

        return permissionMapper.toResponse(savedPermission);
    }

    @Override
    public PermissionResponse update(Long id, PermissionRequest request) {
        log.info("Updating permission with id: {}", id);

        Permission existingPermission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with id: " + id));

        // Normalize code
        String code = request.getCode().trim().toUpperCase();

        // Check if code is being changed and if new code already exists
        if (!existingPermission.getCode().equals(code) &&
            permissionRepository.existsByCode(code)) {
            throw new ExistingResourcesException("Permission with code '" + code + "' already exists");
        }

        // Check if name is being changed and if new name already exists
        String name = request.getName().trim();
        if (!existingPermission.getName().equals(name) &&
            permissionRepository.existsByName(name)) {
            throw new ExistingResourcesException("Permission with name '" + name + "' already exists");
        }

        // Update resource if changed
        if (!existingPermission.getResource().getId().equals(request.getResourceId())) {
            Resource resource = resourceRepository.findById(request.getResourceId())
                    .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + request.getResourceId()));
            existingPermission.setResource(resource);
        }

        // Update basic fields
        existingPermission.setName(name);
        existingPermission.setCode(code);
        existingPermission.setDescription(request.getDescription());

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
        List<Permission> permissions = permissionRepository.findByStatus(CommonStatus.ACTIVE);
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
        log.info("Soft deleting permission with id: {}", id);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with id: " + id));

        // Check if permission is being used by any roles
        if (permission.getRoles() != null && !permission.getRoles().isEmpty()) {
            throw new ExistingResourcesException("Cannot delete permission. It is assigned to " +
                permission.getRoles().size() + " role(s). Please remove it from roles first.");
        }

        // Soft delete - set status to DELETED
        permission.setStatus(CommonStatus.DELETED);
        permission.setVerified(false);
        permissionRepository.save(permission);

        log.info("Permission soft deleted successfully with id: {}", id);
    }

    @Override
    public void toggleActive(Long id) {
        log.info("Toggling status for permission with id: {}", id);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with id: " + id));

        // Toggle between ACTIVE and INACTIVE
        if (permission.getStatus() == CommonStatus.ACTIVE) {
            permission.setStatus(CommonStatus.INACTIVE);
        } else if (permission.getStatus() == CommonStatus.INACTIVE) {
            permission.setStatus(CommonStatus.ACTIVE);
        }

        permissionRepository.save(permission);

        log.info("Permission status toggled to: {} for id: {}", permission.getStatus(), id);
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

    @Override
    public PermissionResponse updateStatus(Long id, CommonStatus status) {
        log.info("Updating status for permission id: {} to {}", id, status);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with id: " + id));

        // Validate status transition
        if (permission.getStatus() == CommonStatus.DELETED) {
            throw new ExistingResourcesException("Cannot update status of deleted permission");
        }

        if (status == CommonStatus.DELETED) {
            throw new ExistingResourcesException("Use delete endpoint to delete permission");
        }

        permission.setStatus(status);
        Permission savedPermission = permissionRepository.save(permission);

        log.info("Permission status updated successfully for id: {}", id);
        return permissionMapper.toResponse(savedPermission);
    }

    @Override
    public PermissionResponse verifyPermission(Long id) {
        log.info("Verifying permission with id: {}", id);

        Permission permission = permissionRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Permission not found with id: " + id));

        if (permission.isVerified()) {
            throw new ExistingResourcesException("Permission is already verified");
        }

        if (permission.getStatus() == CommonStatus.DELETED) {
            throw new ExistingResourcesException("Cannot verify deleted permission");
        }

        permission.setVerified(true);
        Permission savedPermission = permissionRepository.save(permission);

        log.info("Permission verified successfully for id: {}", id);
        return permissionMapper.toResponse(savedPermission);
    }
}
