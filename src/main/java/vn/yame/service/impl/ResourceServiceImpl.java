package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.reponse.ResourceResponse;
import vn.yame.dto.request.ResourceRequest;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.ResourceMapper;
import vn.yame.model.Resource;
import vn.yame.repository.ResourceRepository;
import vn.yame.service.ResourceService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ResourceServiceImpl implements ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources() {
        log.info("Fetching all resources");
        // Fix N+1 query: use findAllWithPermissions instead of findAll
        return resourceRepository.findAllWithPermissions()
                .stream()
                .map(resourceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ResourceResponse> getAllResourcesWithPagination(Pageable pageable) {
        log.info("Fetching resources with pagination - page: {}, size: {}",
                 pageable.getPageNumber(), pageable.getPageSize());
        // Fixed N+1 query: use findAllWithPermissions with Pageable
        Page<Resource> resources = resourceRepository.findAllWithPermissions(pageable);
        return resources.map(resourceMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {
        log.info("Fetching resource with id: {}", id);
        // Use findByIdWithPermissions to eager load permissions
        Resource resource = resourceRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + id));

        // Prevent returning deleted resources
        if (resource.getStatus() == CommonStatus.DELETED) {
            throw new NotFoundResourcesException("Resource not found with id: " + id);
        }

        return resourceMapper.toResponse(resource);
    }

    @Override
    @Transactional(readOnly = true)
    public ResourceResponse getResourceByName(String name) {
        log.info("Fetching resource with name: {}", name);
        Resource resource = resourceRepository.findByName(name)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with name: " + name));

        // Prevent returning deleted resources
        if (resource.getStatus() == CommonStatus.DELETED) {
            throw new NotFoundResourcesException("Resource not found with name: " + name);
        }

        return resourceMapper.toResponse(resource);
    }

    @Override
    public ResourceResponse createResource(ResourceRequest request) {
        log.info("Creating new resource with name: {}", request.getName());

        // Check if resource with same name already exists
        if (resourceRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException("Resource already exists with name: " + request.getName());
        }

        Resource resource = resourceMapper.toEntity(request);
        resource.setStatus(CommonStatus.ACTIVE);
        Resource savedResource = resourceRepository.save(resource);

        log.info("Resource created successfully with id: {}", savedResource.getId());
        return resourceMapper.toResponse(savedResource);
    }

    @Override
    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        log.info("Updating resource with id: {}", id);

        Resource existingResource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + id));

        // Check if another resource with same name exists (excluding current one)
        if (resourceRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ExistingResourcesException("Resource already exists with name: " + request.getName());
        }

        resourceMapper.updateEntityFromRequest(existingResource, request);
        Resource savedResource = resourceRepository.save(existingResource);

        log.info("Resource updated successfully with id: {}", id);
        return resourceMapper.toResponse(savedResource);
    }

    @Override
    public void deleteResource(Long id) {
        log.info("Soft deleting resource with id: {}", id);

        // Use findByIdWithPermissions to eager load permissions
        Resource resource = resourceRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + id));

        // Prevent deleting already deleted resource
        if (resource.getStatus() == CommonStatus.DELETED) {
            throw new NotFoundResourcesException("Resource not found with id: " + id);
        }

        // Check if resource has any permissions
        if (resource.getPermissions() != null && !resource.getPermissions().isEmpty()) {
            throw new ExistingResourcesException("Cannot delete resource. It has " +
                resource.getPermissions().size() + " permission(s) associated with it.");
        }

        resource.setStatus(CommonStatus.DELETED);
        resourceRepository.save(resource);

        log.info("Resource soft deleted successfully with id: {}", id);
    }

    @Override
    public ResourceResponse updateStatus(Long id, CommonStatus status) {
        log.info("Updating status for resource id: {} to {}", id, status);

        // Use findByIdWithPermissions for consistency
        Resource resource = resourceRepository.findByIdWithPermissions(id)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + id));

        // Prevent updating already deleted resource
        if (resource.getStatus() == CommonStatus.DELETED) {
            throw new NotFoundResourcesException("Resource not found with id: " + id);
        }

        // If trying to set status to DELETED, validate permissions
        if (status == CommonStatus.DELETED) {
            if (resource.getPermissions() != null && !resource.getPermissions().isEmpty()) {
                throw new ExistingResourcesException("Cannot delete resource. It has " +
                    resource.getPermissions().size() + " permission(s) associated with it.");
            }
        }

        resource.setStatus(status);
        Resource savedResource = resourceRepository.save(resource);

        log.info("Resource status updated successfully for id: {}", id);
        return resourceMapper.toResponse(savedResource);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getResourcesByStatus(CommonStatus status) {
        log.info("Fetching resources with status: {}", status);
        List<Resource> resources = resourceRepository.findByStatus(status);
        return resources.stream()
                .map(resourceMapper::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<ResourceResponse> getActiveResources() {
        log.info("Fetching active resources");
        List<Resource> resources = resourceRepository.findByStatus(CommonStatus.ACTIVE);
        return resources.stream()
                .map(resourceMapper::toResponse)
                .toList();
    }
}
