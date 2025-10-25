package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.request.ProductVariantRequest;
import vn.yame.dto.reponse.ProductVariantResponse;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.ProductVariantMapper;
import vn.yame.model.Color;
import vn.yame.model.Product;
import vn.yame.model.ProductVariant;
import vn.yame.model.Size;
import vn.yame.repository.ColorRepository;
import vn.yame.repository.ProductRepository;
import vn.yame.repository.ProductVariantRepository;
import vn.yame.repository.SizeRepository;
import vn.yame.service.ProductVariantService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductVariantServiceImpl implements ProductVariantService {

    private final ProductVariantRepository productVariantRepository;
    private final ProductRepository productRepository;
    private final ColorRepository colorRepository;
    private final SizeRepository sizeRepository;
    private final ProductVariantMapper productVariantMapper;

    @Override
    public ProductVariantResponse createProductVariant(ProductVariantRequest request) {
        log.info("Creating new product variant with SKU: {}", request.getSkuCode());

        // Check if SKU code already exists
        if (productVariantRepository.existsBySkuCode(request.getSkuCode())) {
            throw new ExistingResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product variant with SKU code '" + request.getSkuCode() + "' already exists"
            );
        }

        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with id: " + request.getProductId()
            ));

        // Validate color exists
        Color color = colorRepository.findById(request.getColorId())
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color not found with id: " + request.getColorId()
            ));

        // Validate size exists
        Size size = sizeRepository.findById(request.getSizeId())
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SIZE_NOT_FOUND,
                "Size not found with id: " + request.getSizeId()
            ));

        ProductVariant productVariant = productVariantMapper.toEntity(request);
        productVariant.setProduct(product);
        productVariant.setColor(color);
        productVariant.setSize(size);
        productVariant.setStatus(CommonStatus.ACTIVE);

        ProductVariant savedVariant = productVariantRepository.save(productVariant);

        log.info("Product variant created successfully with id: {}", savedVariant.getId());
        return productVariantMapper.toResponse(savedVariant);
    }

    @Override
    public ProductVariantResponse updateProductVariant(Long id, ProductVariantRequest request) {
        log.info("Updating product variant with id: {}", id);

        ProductVariant productVariant = productVariantRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product variant not found with id: " + id
            ));

        // Check if SKU code is being changed and if new SKU code already exists
        if (!productVariant.getSkuCode().equals(request.getSkuCode()) &&
            productVariantRepository.existsBySkuCodeAndIdNot(request.getSkuCode(), id)) {
            throw new ExistingResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product variant with SKU code '" + request.getSkuCode() + "' already exists"
            );
        }

        // Validate product exists
        Product product = productRepository.findById(request.getProductId())
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with id: " + request.getProductId()
            ));

        // Validate color exists
        Color color = colorRepository.findById(request.getColorId())
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color not found with id: " + request.getColorId()
            ));

        // Validate size exists
        Size size = sizeRepository.findById(request.getSizeId())
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SIZE_NOT_FOUND,
                "Size not found with id: " + request.getSizeId()
            ));

        productVariantMapper.updateEntity(productVariant, request);
        productVariant.setProduct(product);
        productVariant.setColor(color);
        productVariant.setSize(size);

        ProductVariant updatedVariant = productVariantRepository.save(productVariant);

        log.info("Product variant updated successfully with id: {}", id);
        return productVariantMapper.toResponse(updatedVariant);
    }

    @Override
    public void deleteProductVariant(Long id) {
        log.info("Soft deleting product variant with id: {}", id);

        ProductVariant productVariant = productVariantRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product variant not found with id: " + id
            ));

        // Soft delete - set status to DELETED
        productVariant.setStatus(CommonStatus.DELETED);
        productVariantRepository.save(productVariant);

        log.info("Product variant soft deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductVariantResponse getProductVariantById(Long id) {
        log.info("Fetching product variant with id: {}", id);

        ProductVariant productVariant = productVariantRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product variant not found with id: " + id
            ));

        return productVariantMapper.toResponse(productVariant);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getAllProductVariants() {
        log.info("Fetching all product variants");

        List<ProductVariant> productVariants = productVariantRepository.findAll();
        return productVariantMapper.toResponseList(productVariants);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductVariantResponse> getAllProductVariantsWithPagination(Pageable pageable) {
        log.info("Fetching product variants with pagination - page: {}, size: {}",
            pageable.getPageNumber(), pageable.getPageSize());

        Page<ProductVariant> variantsPage = productVariantRepository.findAll(pageable);
        return variantsPage.map(productVariantMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getActiveProductVariants() {
        log.info("Fetching all active product variants");

        List<ProductVariant> productVariants = productVariantRepository.findByStatus(CommonStatus.ACTIVE);
        return productVariantMapper.toResponseList(productVariants);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductVariantResponse> getProductVariantsByProductId(Long productId) {
        log.info("Fetching product variants for product id: {}", productId);

        // Validate product exists
        if (!productRepository.existsById(productId)) {
            throw new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with id: " + productId
            );
        }

        List<ProductVariant> productVariants = productVariantRepository.findByProductId(productId);
        return productVariantMapper.toResponseList(productVariants);
    }

    @Override
    public ProductVariantResponse updateStatus(Long id, CommonStatus status) {
        log.info("Updating status of product variant with id: {} to {}", id, status);

        ProductVariant productVariant = productVariantRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product variant not found with id: " + id
            ));

        productVariant.setStatus(status);
        ProductVariant updatedVariant = productVariantRepository.save(productVariant);

        log.info("Product variant status updated successfully");
        return productVariantMapper.toResponse(updatedVariant);
    }

    @Override
    public ProductVariantResponse updateStockQuantity(Long id, int stockQuantity) {
        log.info("Updating stock quantity of product variant with id: {} to {}", id, stockQuantity);

        ProductVariant productVariant = productVariantRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product variant not found with id: " + id
            ));

        productVariant.setStockQuantity(stockQuantity);
        ProductVariant updatedVariant = productVariantRepository.save(productVariant);

        log.info("Product variant stock quantity updated successfully");
        return productVariantMapper.toResponse(updatedVariant);
    }
}

