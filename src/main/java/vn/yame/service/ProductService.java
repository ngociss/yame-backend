package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ProductStatus;
import vn.yame.dto.request.ProductRequest;
import vn.yame.dto.reponse.ProductResponse;

import java.util.List;

public interface ProductService {

    ProductResponse createProduct(ProductRequest request);

    ProductResponse updateProduct(Long id, ProductRequest request);

    void deleteProduct(Long id);

    ProductResponse getProductById(Long id);

    ProductResponse getProductBySlug(String slug);

    List<ProductResponse> getAllProducts();

    Page<ProductResponse> getAllProductsWithPagination(Pageable pageable);

    Page<ProductResponse> getProductsByCategory(Long categoryId, Pageable pageable);

    Page<ProductResponse> getProductsByMaterial(Long materialId, Pageable pageable);

    Page<ProductResponse> searchProducts(Long categoryId, Long materialId,
                                         CommonStatus status, ProductStatus productStatus,
                                         String name, Pageable pageable);

    List<ProductResponse> getActiveProducts();

    ProductResponse updateStatus(Long id, CommonStatus status);

    ProductResponse updateProductStatus(Long id, ProductStatus productStatus);
}

