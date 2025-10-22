package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.dto.request.ProductGroupRequest;
import vn.yame.dto.reponse.ProductGroupResponse;

import java.util.List;

public interface ProductGroupService {

    ProductGroupResponse createProductGroup(ProductGroupRequest request);

    ProductGroupResponse updateProductGroup(Long id, ProductGroupRequest request);

    void deleteProductGroup(Long id);

    ProductGroupResponse getProductGroupById(Long id);

    List<ProductGroupResponse> getAllProductGroups();

    Page<ProductGroupResponse> getAllProductGroupsWithPagination(Pageable pageable);

    List<ProductGroupResponse> searchProductGroupsByName(String name);
}

