package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import vn.yame.dto.reponse.ProductVariantResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.ProductVariantRequest;
import vn.yame.dto.request.UpdateStatusRequest;
import vn.yame.service.ProductVariantService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-variants")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Product Variant Management", description = "APIs for managing product variants (size, color combinations)")
public class ProductVariantController {

    private final ProductVariantService productVariantService;

    @PostMapping
    @Operation(
        summary = "Create a new product variant",
        description = "Create a new product variant with product, color, size, SKU code and stock quantity"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product variant created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or SKU code already exists"),
        @ApiResponse(responseCode = "404", description = "Product, Color or Size not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductVariantResponse>> createProductVariant(
            @Valid @RequestBody ProductVariantRequest request) {
        log.info("REST request to create product variant: {}", request.getSkuCode());
        ProductVariantResponse response = productVariantService.createProductVariant(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Product variant created successfully",
                response
            ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing product variant",
        description = "Update product variant information by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product variant updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product variant not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductVariantResponse>> updateProductVariant(
            @Parameter(description = "Product Variant ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductVariantRequest request) {
        log.info("REST request to update product variant with id: {}", id);

        ProductVariantResponse response = productVariantService.updateProductVariant(id, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product variant updated successfully",
            response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a product variant",
        description = "Soft delete a product variant by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product variant deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product variant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteProductVariant(
            @Parameter(description = "Product Variant ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete product variant with id: {}", id);

        productVariantService.deleteProductVariant(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product variant deleted successfully",
            null
        ));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get product variant by ID",
        description = "Retrieve a specific product variant by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product variant found"),
        @ApiResponse(responseCode = "404", description = "Product variant not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductVariantResponse>> getProductVariantById(
            @Parameter(description = "Product Variant ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get product variant with id: {}", id);

        ProductVariantResponse response = productVariantService.getProductVariantById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product variant retrieved successfully",
            response
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all product variants",
        description = "Retrieve all product variants without pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product variants retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductVariantResponse>>> getAllProductVariants() {
        log.info("REST request to get all product variants");

        List<ProductVariantResponse> response = productVariantService.getAllProductVariants();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product variants retrieved successfully",
            response
        ));
    }

    @GetMapping("/pagination")
    @Operation(
        summary = "Get all product variants with pagination",
        description = "Retrieve product variants with pagination and sorting support"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product variants retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<ProductVariantResponse>>> getAllProductVariantsWithPagination(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "id") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String sortDirection) {

        log.info("REST request to get product variants with pagination - page: {}, size: {}", page, size);

        Sort sort = sortDirection.equalsIgnoreCase("asc")
            ? Sort.by(sortBy).ascending()
            : Sort.by(sortBy).descending();

        Pageable pageable = PageRequest.of(page, size, sort);
        Page<ProductVariantResponse> response = productVariantService.getAllProductVariantsWithPagination(pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product variants retrieved successfully",
            response
        ));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get all active product variants",
        description = "Retrieve all product variants with ACTIVE status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active product variants retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductVariantResponse>>> getActiveProductVariants() {
        log.info("REST request to get all active product variants");

        List<ProductVariantResponse> response = productVariantService.getActiveProductVariants();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Active product variants retrieved successfully",
            response
        ));
    }

    @GetMapping("/product/{productId}")
    @Operation(
        summary = "Get product variants by product ID",
        description = "Retrieve all variants for a specific product"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product variants retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductVariantResponse>>> getProductVariantsByProductId(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long productId) {
        log.info("REST request to get product variants for product id: {}", productId);

        List<ProductVariantResponse> response = productVariantService.getProductVariantsByProductId(productId);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product variants retrieved successfully",
            response
        ));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update product variant status",
        description = "Update the status of a product variant (ACTIVE, INACTIVE, DELETED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product variant not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductVariantResponse>> updateStatus(
            @Parameter(description = "Product Variant ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("REST request to update status of product variant with id: {} to {}", id, request.getStatus());

        ProductVariantResponse response = productVariantService.updateStatus(id, request.getStatus());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product variant status updated successfully",
            response
        ));
    }

    @PatchMapping("/{id}/stock")
    @Operation(
        summary = "Update product variant stock quantity",
        description = "Update the stock quantity of a product variant"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Stock quantity updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product variant not found"),
        @ApiResponse(responseCode = "400", description = "Invalid stock quantity"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductVariantResponse>> updateStockQuantity(
            @Parameter(description = "Product Variant ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "New stock quantity", required = true)
            @RequestParam int stockQuantity) {
        log.info("REST request to update stock quantity of product variant with id: {} to {}", id, stockQuantity);

        ProductVariantResponse response = productVariantService.updateStockQuantity(id, stockQuantity);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product variant stock quantity updated successfully",
            response
        ));
    }
}

