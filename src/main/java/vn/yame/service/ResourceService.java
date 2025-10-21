package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.reponse.ResourceResponse;
import vn.yame.dto.request.ResourceRequest;

import java.util.List;

public interface ResourceService {

    List<ResourceResponse> getAllResources();

    Page<ResourceResponse> getAllResourcesWithPagination(Pageable pageable);

    ResourceResponse getResourceById(Long id);

    ResourceResponse getResourceByName(String name);

    ResourceResponse createResource(ResourceRequest request);

    ResourceResponse updateResource(Long id, ResourceRequest request);

    void deleteResource(Long id);

    ResourceResponse updateStatus(Long id, CommonStatus status);

    List<ResourceResponse> getResourcesByStatus(CommonStatus status);

    List<ResourceResponse> getActiveResources();
}
