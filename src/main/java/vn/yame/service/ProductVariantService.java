package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.request.ProductVariantRequest;
import vn.yame.dto.reponse.ProductVariantResponse;

import java.util.List;

public interface ProductVariantService {

    ProductVariantResponse createProductVariant(ProductVariantRequest request);

    ProductVariantResponse updateProductVariant(Long id, ProductVariantRequest request);

    void deleteProductVariant(Long id);

    ProductVariantResponse getProductVariantById(Long id);

    List<ProductVariantResponse> getAllProductVariants();

    Page<ProductVariantResponse> getAllProductVariantsWithPagination(Pageable pageable);

    List<ProductVariantResponse> getActiveProductVariants();

    List<ProductVariantResponse> getProductVariantsByProductId(Long productId);

    ProductVariantResponse updateStatus(Long id, CommonStatus status);

    ProductVariantResponse updateStockQuantity(Long id, int stockQuantity);
}

