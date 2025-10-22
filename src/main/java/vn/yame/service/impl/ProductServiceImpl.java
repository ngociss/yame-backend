package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ErrorCode;
import vn.yame.common.enums.ProductStatus;
import vn.yame.dto.request.ProductRequest;
import vn.yame.dto.reponse.ProductResponse;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.InvalidDataException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.ProductMapper;
import vn.yame.model.Category;
import vn.yame.model.Material;
import vn.yame.model.Product;
import vn.yame.repository.CategoryRepository;
import vn.yame.repository.MaterialRepository;
import vn.yame.repository.ProductRepository;
import vn.yame.service.ProductService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;
    private final CategoryRepository categoryRepository;
    private final MaterialRepository materialRepository;
    private final ProductMapper productMapper;

    @Override
    public ProductResponse createProduct(ProductRequest request) {
        log.info("Creating new product with name: {}", request.getName());

        // Check if slug already exists
        if (productRepository.existsBySlug(request.getSlug())) {
            throw new ExistingResourcesException(
                ErrorCode.PRODUCT_SLUG_DUPLICATE,
                "Product with slug '" + request.getSlug() + "' already exists"
            );
        }

        // Check if name already exists
        if (productRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.PRODUCT_ALREADY_EXISTS,
                "Product with name '" + request.getName() + "' already exists"
            );
        }

        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found with id: " + request.getCategoryId()
            ));

        // Validate material if provided
        Material material = null;
        if (request.getMaterialId() != null) {
            material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new NotFoundResourcesException(
                    ErrorCode.MATERIAL_NOT_FOUND,
                    "Material not found with id: " + request.getMaterialId()
                ));
        }

        // Validate discount price
        if (request.getDiscountPrice() != null &&
            request.getDiscountPrice().compareTo(request.getBasePrice()) > 0) {
            throw new InvalidDataException(
                ErrorCode.INVALID_REQUEST,
                "Discount price cannot be greater than base price"
            );
        }

        Product product = productMapper.toEntity(request);
        product.setCategory(category);
        product.setMaterial(material);
        product.setStatus(CommonStatus.ACTIVE);
        product.setProductStatus(ProductStatus.ACTIVE);

        Product savedProduct = productRepository.save(product);

        log.info("Product created successfully with id: {}", savedProduct.getId());
        return productMapper.toResponse(savedProduct);
    }

    @Override
    public ProductResponse updateProduct(Long id, ProductRequest request) {
        log.info("Updating product with id: {}", id);

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with id: " + id
            ));

        // Check if slug is being changed and if new slug already exists
        if (!product.getSlug().equals(request.getSlug()) &&
            productRepository.existsBySlug(request.getSlug())) {
            throw new ExistingResourcesException(
                ErrorCode.PRODUCT_SLUG_DUPLICATE,
                "Product with slug '" + request.getSlug() + "' already exists"
            );
        }

        // Check if name is being changed and if new name already exists
        if (!product.getName().equals(request.getName()) &&
            productRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.PRODUCT_ALREADY_EXISTS,
                "Product with name '" + request.getName() + "' already exists"
            );
        }

        // Validate category exists
        Category category = categoryRepository.findById(request.getCategoryId())
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found with id: " + request.getCategoryId()
            ));

        // Validate material if provided
        Material material = null;
        if (request.getMaterialId() != null) {
            material = materialRepository.findById(request.getMaterialId())
                .orElseThrow(() -> new NotFoundResourcesException(
                    ErrorCode.MATERIAL_NOT_FOUND,
                    "Material not found with id: " + request.getMaterialId()
                ));
        }

        // Validate discount price
        if (request.getDiscountPrice() != null &&
            request.getDiscountPrice().compareTo(request.getBasePrice()) > 0) {
            throw new InvalidDataException(
                ErrorCode.INVALID_REQUEST,
                "Discount price cannot be greater than base price"
            );
        }

        productMapper.updateEntity(product, request);
        product.setCategory(category);
        product.setMaterial(material);

        Product updatedProduct = productRepository.save(product);

        log.info("Product updated successfully with id: {}", id);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public void deleteProduct(Long id) {
        log.info("Soft deleting product with id: {}", id);

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with id: " + id
            ));

        // Soft delete - set status to DELETED
        product.setStatus(CommonStatus.DELETED);
        product.setProductStatus(ProductStatus.DISCONTINUED);
        productRepository.save(product);

        log.info("Product soft deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductById(Long id) {
        log.info("Fetching product with id: {}", id);

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with id: " + id
            ));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public ProductResponse getProductBySlug(String slug) {
        log.info("Fetching product with slug: {}", slug);

        Product product = productRepository.findBySlug(slug)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with slug: " + slug
            ));

        return productMapper.toResponse(product);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getAllProducts() {
        log.info("Fetching all products");

        List<Product> products = productRepository.findAll();
        return productMapper.toResponseList(products);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getAllProductsWithPagination(Pageable pageable) {
        log.info("Fetching products with pagination - page: {}, size: {}",
            pageable.getPageNumber(), pageable.getPageSize());

        Page<Product> productsPage = productRepository.findAll(pageable);
        return productsPage.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable) {
        log.info("Fetching products by category id: {}", categoryId);

        // Validate category exists
        if (!categoryRepository.existsById(categoryId)) {
            throw new NotFoundResourcesException(
                ErrorCode.CATEGORY_NOT_FOUND,
                "Category not found with id: " + categoryId
            );
        }

        Page<Product> productsPage = productRepository.findByCategoryId(categoryId, pageable);
        return productsPage.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> getProductsByMaterial(Long materialId, Pageable pageable) {
        log.info("Fetching products by material id: {}", materialId);

        // Validate material exists
        if (!materialRepository.existsById(materialId)) {
            throw new NotFoundResourcesException(
                ErrorCode.MATERIAL_NOT_FOUND,
                "Material not found with id: " + materialId
            );
        }

        Page<Product> productsPage = productRepository.findByMaterialId(materialId, pageable);
        return productsPage.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ProductResponse> searchProducts(Long categoryId, Long materialId,
                                                 CommonStatus status, ProductStatus productStatus,
                                                 String name, Pageable pageable) {
        log.info("Searching products with filters - category: {}, material: {}, status: {}, productStatus: {}, name: {}",
            categoryId, materialId, status, productStatus, name);

        Page<Product> productsPage = productRepository.searchProducts(
            categoryId, materialId, status, productStatus, name, pageable
        );
        return productsPage.map(productMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductResponse> getActiveProducts() {
        log.info("Fetching all active products");

        List<Product> products = productRepository.findByStatus(CommonStatus.ACTIVE);
        return productMapper.toResponseList(products);
    }

    @Override
    public ProductResponse updateStatus(Long id, CommonStatus status) {
        log.info("Updating status for product with id: {} to {}", id, status);

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with id: " + id
            ));

        product.setStatus(status);
        Product updatedProduct = productRepository.save(product);

        log.info("Product status updated successfully with id: {}", id);
        return productMapper.toResponse(updatedProduct);
    }

    @Override
    public ProductResponse updateProductStatus(Long id, ProductStatus productStatus) {
        log.info("Updating product status for product with id: {} to {}", id, productStatus);

        Product product = productRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.PRODUCT_NOT_FOUND,
                "Product not found with id: " + id
            ));

        product.setProductStatus(productStatus);
        Product updatedProduct = productRepository.save(product);

        log.info("Product status updated successfully with id: {}", id);
        return productMapper.toResponse(updatedProduct);
    }
}

