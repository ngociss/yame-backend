package vn.yame.controller;


import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.yame.dto.reponse.ProductImageResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.ProductImageRequest;
import vn.yame.service.ProductImageService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-images")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Product Image Management", description = "APIs for managing product images")
public class ProductImageController {
    private final ProductImageService productImageService;

    // ============ OPTION 1: Upload trực tiếp vào product (TỰ ĐỘNG HÓA - KHUYẾN NGHỊ) ============

    @PostMapping(value = "/product/{productId}/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload image directly to product (RECOMMENDED)",
        description = "Upload an image file and automatically save to product with metadata in one step"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Image uploaded and added to product successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductImageResponse>> uploadImageToProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId,
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("file") MultipartFile file,
            @Parameter(description = "Alt text for SEO")
            @RequestParam(required = false) String altText,
            @Parameter(description = "Set as primary image")
            @RequestParam(defaultValue = "false") boolean isPrimary,
            @Parameter(description = "Display order")
            @RequestParam(defaultValue = "0") int displayOrder) {

        log.info("REST request to upload image directly to product: {} - file: {}",
                 productId, file.getOriginalFilename());

        ProductImageResponse response = productImageService.uploadImageToProduct(
            productId, file, altText, isPrimary, displayOrder
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Image uploaded and added to product successfully",
                response
            ));
    }

    // ============ NEW: Upload NHIỀU ảnh cùng lúc vào product (BEST UX) ============

    @PostMapping(value = "/product/{productId}/upload-multiple", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload multiple images to product (BATCH UPLOAD)",
        description = "Upload multiple image files at once and automatically save to product. First image will be set as primary by default."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Images uploaded and added to product successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductImageResponse>>> uploadMultipleImagesToProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId,
            @Parameter(description = "Multiple image files to upload", required = true)
            @RequestParam("files") List<MultipartFile> files,
            @Parameter(description = "Set first image as primary")
            @RequestParam(defaultValue = "true") boolean setFirstAsPrimary) {

        log.info("REST request to upload {} images to product: {}", files.size(), productId);

        List<ProductImageResponse> responses = productImageService.uploadMultipleImagesToProduct(
            productId, files, setFirstAsPrimary
        );

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                files.size() + " images uploaded and added to product successfully",
                responses
            ));
    }

    // ============ OPTION 2: Upload riêng lấy URL (LINH HOẠT) ============

    @PostMapping(value = "/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(
        summary = "Upload single product image (Get URL only)",
        description = "Upload an image file and get URL (without saving to database). Use this when you need more control."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image uploaded successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid file format or size"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<String>> uploadImage(
            @Parameter(description = "Image file to upload", required = true)
            @RequestParam("file") MultipartFile file) {
        log.info("REST request to upload product image: {}", file.getOriginalFilename());

        String imageUrl = productImageService.uploadProductImage(file);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Image uploaded successfully",
            imageUrl
        ));
    }

    // ============ OPTION 3: Thêm ảnh từ URL có sẵn (REUSE) ============

    @PostMapping("/product/{productId}")
    @Operation(
        summary = "Add image to product from existing URL",
        description = "Add an image with metadata to a specific product using an existing URL"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Image added to product successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductImageResponse>> addImageToProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId,
            @Valid @RequestBody ProductImageRequest request) {
        log.info("REST request to add image to product: {}", productId);

        ProductImageResponse response = productImageService.addImageToProduct(productId, request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Image added to product successfully",
                response
            ));
    }

    @GetMapping("/product/{productId}")
    @Operation(
        summary = "Get all images of a product",
        description = "Retrieve all images for a specific product, ordered by displayOrder"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Images retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductImageResponse>>> getProductImages(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId) {
        log.info("REST request to get images for product: {}", productId);

        List<ProductImageResponse> response = productImageService.getProductImages(productId);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product images retrieved successfully",
            response
        ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update product image metadata",
        description = "Update altText, isPrimary, displayOrder of an image"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image updated successfully"),
        @ApiResponse(responseCode = "404", description = "Image not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductImageResponse>> updateProductImage(
            @Parameter(description = "Product Image ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductImageRequest request) {
        log.info("REST request to update product image: {}", id);

        ProductImageResponse response = productImageService.updateProductImage(id, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product image updated successfully",
            response
        ));
    }

    @PatchMapping("/{id}/set-primary")
    @Operation(
        summary = "Set image as primary",
        description = "Set an image as the primary image for the product (unset other primary images)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Primary image set successfully"),
        @ApiResponse(responseCode = "404", description = "Image not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductImageResponse>> setPrimaryImage(
            @Parameter(description = "Product Image ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to set primary image: {}", id);

        ProductImageResponse response = productImageService.setPrimaryImage(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Primary image set successfully",
            response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete product image",
        description = "Delete a product image by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Image deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Image not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteProductImage(
            @Parameter(description = "Product Image ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete product image: {}", id);

        productImageService.deleteProductImage(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product image deleted successfully",
            null
        ));
    }

    @DeleteMapping("/product/{productId}/all")
    @Operation(
        summary = "Delete all images of a product (BATCH DELETE)",
        description = "Delete all images associated with a product. Useful when resetting product images or deleting product."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "All images deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<String>> deleteAllProductImages(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId) {
        log.info("REST request to delete all images of product: {}", productId);

        int deletedCount = productImageService.deleteAllProductImages(productId);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            deletedCount + " images deleted successfully",
            "Deleted " + deletedCount + " images from product " + productId
        ));
    }

    @PatchMapping("/product/{productId}/reorder")
    @Operation(
        summary = "Reorder product images (DRAG & DROP)",
        description = "Update display order of multiple images at once. Perfect for drag & drop UI."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Images reordered successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid image IDs"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductImageResponse>>> reorderProductImages(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId,
            @Parameter(description = "Array of image IDs in new order", required = true)
            @RequestBody List<Long> imageIds) {
        log.info("REST request to reorder {} images of product: {}", imageIds.size(), productId);

        List<ProductImageResponse> responses = productImageService.reorderProductImages(productId, imageIds);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Images reordered successfully",
            responses
        ));
    }
}
