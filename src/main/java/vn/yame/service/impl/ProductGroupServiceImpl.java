package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.request.ProductGroupRequest;
import vn.yame.dto.reponse.ProductGroupResponse;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.ProductGroupMapper;
import vn.yame.model.ProductGroup;
import vn.yame.repository.ProductGroupRepository;
import vn.yame.service.ProductGroupService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductGroupServiceImpl implements ProductGroupService {

    private final ProductGroupRepository productGroupRepository;
    private final ProductGroupMapper productGroupMapper;

    @Override
    public ProductGroupResponse createProductGroup(ProductGroupRequest request) {
        log.info("Creating new product group with name: {}", request.getName());

        // Check if name already exists
        if (productGroupRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.INVALID_REQUEST,
                "Product group with name '" + request.getName() + "' already exists"
            );
        }

        ProductGroup productGroup = productGroupMapper.toEntity(request);
        ProductGroup savedProductGroup = productGroupRepository.save(productGroup);

        log.info("Product group created successfully with id: {}", savedProductGroup.getId());
        return productGroupMapper.toResponse(savedProductGroup);
    }

    @Override
    public ProductGroupResponse updateProductGroup(Long id, ProductGroupRequest request) {
        log.info("Updating product group with id: {}", id);

        ProductGroup productGroup = productGroupRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.INVALID_REQUEST,
                "Product group not found with id: " + id
            ));

        // Check if name is being changed and if new name already exists
        if (!productGroup.getName().equals(request.getName()) &&
            productGroupRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.INVALID_REQUEST,
                "Product group with name '" + request.getName() + "' already exists"
            );
        }

        productGroupMapper.updateEntity(productGroup, request);
        ProductGroup updatedProductGroup = productGroupRepository.save(productGroup);

        log.info("Product group updated successfully with id: {}", id);
        return productGroupMapper.toResponse(updatedProductGroup);
    }

    @Override
    public void deleteProductGroup(Long id) {
        log.info("Deleting product group with id: {}", id);

        ProductGroup productGroup = productGroupRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.INVALID_REQUEST,
                "Product group not found with id: " + id
            ));

        // Check if product group is being used by products (when product relationship is implemented)
        // if (productGroup.getProducts() != null && !productGroup.getProducts().isEmpty()) {
        //     throw new InvalidDataException(
        //         ErrorCode.INVALID_REQUEST,
        //         "Cannot delete product group with existing products"
        //     );
        // }

        productGroupRepository.delete(productGroup);

        log.info("Product group deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductGroupResponse getProductGroupById(Long id) {
        log.info("Fetching product group with id: {}", id);

        ProductGroup productGroup = productGroupRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.INVALID_REQUEST,
                "Product group not found with id: " + id
            ));

        return productGroupMapper.toResponse(productGroup);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductGroupResponse> getAllProductGroups() {
        log.info("Fetching all product groups");

        List<ProductGroup> productGroups = productGroupRepository.findAll();
        return productGroupMapper.toResponseList(productGroups);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductGroupResponse> getAllProductGroupsWithPagination(Pageable pageable) {
        log.info("Fetching product groups with pagination - page: {}, size: {}",
            pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductGroup> productGroupsPage = productGroupRepository.findAll(pageable);
        return productGroupsPage.map(productGroupMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductGroupResponse> searchProductGroupsByName(String name) {
        log.info("Searching product groups by name: {}", name);

        List<ProductGroup> productGroups = productGroupRepository.findByNameContainingIgnoreCase(name);
        return productGroupMapper.toResponseList(productGroups);
    }
}

