package vn.yame.service;

import lombok.RequiredArgsConstructor;
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

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ResourceService {

    private final ResourceRepository resourceRepository;
    private final ResourceMapper resourceMapper;


    @Transactional(readOnly = true)
    public List<ResourceResponse> getAllResources() {
        return resourceRepository.findAll()
                .stream()
                .map(resourceMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResourceById(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + id));
        return resourceMapper.toResponse(resource);
    }

    @Transactional(readOnly = true)
    public ResourceResponse getResourceByName(String name) {
        Resource resource = resourceRepository.findByName(name)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with name: " + name));
        return resourceMapper.toResponse(resource);
    }

    public ResourceResponse createResource(ResourceRequest request) {
        // Check if resource with same name already exists
        if (resourceRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException("Resource already exists with name: " + request.getName());
        }

        Resource resource = resourceMapper.toEntity(request);
        resource.setStatus(CommonStatus.ACTIVE);
        Resource savedResource = resourceRepository.save(resource);
        return resourceMapper.toResponse(savedResource);
    }

    public ResourceResponse updateResource(Long id, ResourceRequest request) {
        Resource existingResource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + id));

        // Check if another resource with same name exists (excluding current one)
        if (resourceRepository.existsByNameAndIdNot(request.getName(), id)) {
            throw new ExistingResourcesException("Resource already exists with name: " + request.getName());
        }

        resourceMapper.updateEntityFromRequest(existingResource, request);
        Resource savedResource = resourceRepository.save(existingResource);
        return resourceMapper.toResponse(savedResource);
    }

    public void deleteResource(Long id) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + id));
        // Check if resource has any permissions
        if (!resource.getPermissions().isEmpty()) {
            throw new ExistingResourcesException("Cannot delete resource. It has " +
                resource.getPermissions().size() + " permission(s) associated with it.");
        }
       resource.setStatus(CommonStatus.DELETED);
         resourceRepository.save(resource);
    }

    public ResourceResponse updateStatus(Long id, CommonStatus status) {
        Resource resource = resourceRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException("Resource not found with id: " + id));

        resource.setStatus(status);
        Resource savedResource = resourceRepository.save(resource);
        return resourceMapper.toResponse(savedResource);
    }
}
