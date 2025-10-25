package vn.yame.service.impl;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.request.ProductImageRequest;
import vn.yame.dto.reponse.ProductImageResponse;
import vn.yame.exception.InvalidDataException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.ProductImageMapper;
import vn.yame.model.Product;
import vn.yame.model.ProductImage;
import vn.yame.repository.ProductImageRepository;
import vn.yame.repository.ProductRepository;
import vn.yame.service.ProductImageService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final Cloudinary cloudinary;
    private final ProductImageRepository productImageRepository;
    private final ProductRepository productRepository;
    private final ProductImageMapper productImageMapper;

    @Override
    public String uploadProductImage(MultipartFile file) {
        try {
            log.info("Uploading product image: {}", file.getOriginalFilename());

            Map uploadResult = cloudinary.uploader().upload(file.getBytes(),
                    ObjectUtils.asMap(
                            "folder", "products/",
                            "use_filename", true,
                            "unique_filename", true,
                            "overwrite", false
                    ));

            String imageUrl = uploadResult.get("secure_url").toString();
            log.info("Image uploaded successfully: {}", imageUrl);
            return imageUrl;
        } catch (IOException e) {
            log.error("Image upload failed", e);
            throw new RuntimeException("Image upload failed", e);
        }
    }

    @Override
    public ProductImageResponse uploadImageToProduct(Long productId, MultipartFile file,
                                                     String altText, boolean isPrimary, int displayOrder) {
        log.info("Uploading image directly to product id: {} - file: {}", productId, file.getOriginalFilename());

        // Step 1: Upload file lên Cloudinary
        String imageUrl = uploadProductImage(file);

        // Step 2: Tự động tạo alt text nếu không có
        if (altText == null || altText.trim().isEmpty()) {
            altText = file.getOriginalFilename();
        }

        // Step 3: Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with id: " + productId
                ));

        // Step 4: If this image is set as primary, unset other primary images
        if (isPrimary) {
            unsetOtherPrimaryImages(productId);
        }

        // Step 5: Tự động set displayOrder nếu = 0
        if (displayOrder == 0) {
            List<ProductImage> existingImages = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
            displayOrder = existingImages.size() + 1;
        }

        // Step 6: Save to database
        ProductImage productImage = new ProductImage();
        productImage.setImageUrl(imageUrl);
        productImage.setAltText(altText);
        productImage.setPrimary(isPrimary);
        productImage.setDisplayOrder(displayOrder);
        productImage.setProduct(product);

        ProductImage savedImage = productImageRepository.save(productImage);

        log.info("Image uploaded and added to product successfully with id: {}", savedImage.getId());
        return productImageMapper.toResponse(savedImage);
    }

    @Override
    public List<ProductImageResponse> uploadMultipleImagesToProduct(Long productId, List<MultipartFile> files,
                                                                    boolean setFirstAsPrimary) {
        log.info("Uploading {} images to product id: {}", files.size(), productId);

        // Validate product exists
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with id: " + productId
                ));

        // Validate files not empty
        if (files == null || files.isEmpty()) {
            throw new InvalidDataException(
                    ErrorCode.INVALID_REQUEST,
                    "No files provided for upload"
            );
        }

        // Get current max displayOrder
        List<ProductImage> existingImages = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        int currentMaxOrder = existingImages.isEmpty() ? 0 :
                              existingImages.stream()
                                           .mapToInt(ProductImage::getDisplayOrder)
                                           .max()
                                           .orElse(0);

        List<ProductImageResponse> responses = new ArrayList<>();

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);

            try {
                // Upload từng file lên Cloudinary
                String imageUrl = uploadProductImage(file);

                // Tạo alt text từ tên file
                String altText = file.getOriginalFilename();

                // Ảnh đầu tiên sẽ là primary nếu setFirstAsPrimary = true
                boolean isPrimary = (i == 0) && setFirstAsPrimary;

                // Unset primary images nếu ảnh này được set là primary
                if (isPrimary) {
                    unsetOtherPrimaryImages(productId);
                }

                // Auto increment displayOrder
                int displayOrder = currentMaxOrder + i + 1;

                // Save to database
                ProductImage productImage = new ProductImage();
                productImage.setImageUrl(imageUrl);
                productImage.setAltText(altText);
                productImage.setPrimary(isPrimary);
                productImage.setDisplayOrder(displayOrder);
                productImage.setProduct(product);

                ProductImage savedImage = productImageRepository.save(productImage);
                responses.add(productImageMapper.toResponse(savedImage));

                log.info("Image {}/{} uploaded successfully", i + 1, files.size());

            } catch (Exception e) {
                log.error("Failed to upload image {}/{}: {}", i + 1, files.size(), file.getOriginalFilename(), e);
                // Có thể throw exception hoặc tiếp tục với các file còn lại
                // Ở đây tôi chọn throw để đảm bảo tất cả ảnh đều upload thành công
                throw new RuntimeException("Failed to upload image: " + file.getOriginalFilename(), e);
            }
        }

        log.info("Successfully uploaded {} images to product id: {}", responses.size(), productId);
        return responses;
    }

    @Override
    public ProductImageResponse addImageToProduct(Long productId, ProductImageRequest request) {
        log.info("Adding image to product id: {}", productId);

        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product not found with id: " + productId
                ));

        // If this image is set as primary, unset other primary images
        if (request.isPrimary()) {
            unsetOtherPrimaryImages(productId);
        }

        ProductImage productImage = productImageMapper.toEntity(request);
        productImage.setProduct(product);

        ProductImage savedImage = productImageRepository.save(productImage);

        log.info("Image added to product successfully with id: {}", savedImage.getId());
        return productImageMapper.toResponse(savedImage);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ProductImageResponse> getProductImages(Long productId) {
        log.info("Fetching images for product id: {}", productId);

        // Validate product exists
        if (!productRepository.existsById(productId)) {
            throw new NotFoundResourcesException(
                    ErrorCode.PRODUCT_NOT_FOUND,
                    "Product not found with id: " + productId
            );
        }

        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        return productImageMapper.toResponseList(images);
    }

    @Override
    public ProductImageResponse updateProductImage(Long id, ProductImageRequest request) {
        log.info("Updating product image with id: {}", id);

        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product image not found with id: " + id
                ));

        // If this image is being set as primary, unset other primary images
        if (request.isPrimary() && !productImage.isPrimary()) {
            unsetOtherPrimaryImages(productImage.getProduct().getId());
        }

        productImageMapper.updateEntity(productImage, request);
        ProductImage updatedImage = productImageRepository.save(productImage);

        log.info("Product image updated successfully with id: {}", id);
        return productImageMapper.toResponse(updatedImage);
    }

    @Override
    public ProductImageResponse setPrimaryImage(Long id) {
        log.info("Setting primary image with id: {}", id);

        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product image not found with id: " + id
                ));

        // Unset other primary images for this product
        unsetOtherPrimaryImages(productImage.getProduct().getId());

        // Set this image as primary
        productImage.setPrimary(true);
        ProductImage updatedImage = productImageRepository.save(productImage);

        log.info("Primary image set successfully");
        return productImageMapper.toResponse(updatedImage);
    }

    @Override
    public void deleteProductImage(Long id) {
        log.info("Deleting product image with id: {}", id);

        ProductImage productImage = productImageRepository.findById(id)
                .orElseThrow(() -> new NotFoundResourcesException(
                        ErrorCode.PRODUCT_NOT_FOUND,
                        "Product image not found with id: " + id
                ));

        productImageRepository.delete(productImage);
        log.info("Product image deleted successfully with id: {}", id);
    }

    @Override
    public int deleteAllProductImages(Long productId) {
        log.info("Deleting all images of product id: {}", productId);

        // Validate product exists
        if (!productRepository.existsById(productId)) {
            throw new NotFoundResourcesException(
                    ErrorCode.PRODUCT_NOT_FOUND,
                    "Product not found with id: " + productId
            );
        }

        List<ProductImage> images = productImageRepository.findByProductIdOrderByDisplayOrderAsc(productId);
        int count = images.size();

        if (!images.isEmpty()) {
            productImageRepository.deleteAll(images);
            log.info("Deleted {} images from product id: {}", count, productId);
        }

        return count;
    }

    @Override
    public List<ProductImageResponse> reorderProductImages(Long productId, List<Long> imageIds) {
        log.info("Reordering {} images for product id: {}", imageIds.size(), productId);

        // Validate product exists
        if (!productRepository.existsById(productId)) {
            throw new NotFoundResourcesException(
                    ErrorCode.PRODUCT_NOT_FOUND,
                    "Product not found with id: " + productId
            );
        }

        // Validate imageIds not empty
        if (imageIds == null || imageIds.isEmpty()) {
            throw new InvalidDataException(
                    ErrorCode.INVALID_REQUEST,
                    "Image IDs list cannot be empty"
            );
        }

        List<ProductImage> updatedImages = new ArrayList<>();

        for (int i = 0; i < imageIds.size(); i++) {
            Long imageId = imageIds.get(i);

            ProductImage image = productImageRepository.findById(imageId)
                    .orElseThrow(() -> new NotFoundResourcesException(
                            ErrorCode.PRODUCT_NOT_FOUND,
                            "Product image not found with id: " + imageId
                    ));

            // Validate image belongs to this product
            if (!image.getProduct().getId().equals(productId)) {
                throw new InvalidDataException(
                        ErrorCode.INVALID_REQUEST,
                        "Image " + imageId + " does not belong to product " + productId
                );
            }

            // Update displayOrder (1-based index)
            image.setDisplayOrder(i + 1);
            updatedImages.add(image);
        }

        // Save all updated images
        List<ProductImage> savedImages = productImageRepository.saveAll(updatedImages);

        log.info("Successfully reordered {} images for product id: {}", savedImages.size(), productId);
        return productImageMapper.toResponseList(savedImages);
    }

    private void unsetOtherPrimaryImages(Long productId) {
        List<ProductImage> primaryImages = productImageRepository.findByProductIdAndIsPrimaryTrue(productId);
        for (ProductImage image : primaryImages) {
            image.setPrimary(false);
        }
        if (!primaryImages.isEmpty()) {
            productImageRepository.saveAll(primaryImages);
            log.info("Unset {} primary images for product id: {}", primaryImages.size(), productId);
        }
    }
}
