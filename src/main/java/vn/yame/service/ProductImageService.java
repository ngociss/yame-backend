package vn.yame.service;

import org.springframework.web.multipart.MultipartFile;
import vn.yame.dto.request.ProductImageRequest;
import vn.yame.dto.reponse.ProductImageResponse;

import java.util.List;

public interface ProductImageService {

    String uploadProductImage(MultipartFile file);

    // Method upload 1 ảnh vào product
    ProductImageResponse uploadImageToProduct(Long productId, MultipartFile file,
                                             String altText, boolean isPrimary, int displayOrder);

    // Method mới: Upload NHIỀU ảnh vào product
    List<ProductImageResponse> uploadMultipleImagesToProduct(Long productId, List<MultipartFile> files,
                                                             boolean setFirstAsPrimary);

    ProductImageResponse addImageToProduct(Long productId, ProductImageRequest request);

    List<ProductImageResponse> getProductImages(Long productId);

    ProductImageResponse updateProductImage(Long id, ProductImageRequest request);

    ProductImageResponse setPrimaryImage(Long id);

    void deleteProductImage(Long id);

    int deleteAllProductImages(Long productId);

    List<ProductImageResponse> reorderProductImages(Long productId, List<Long> imageIds);
}
