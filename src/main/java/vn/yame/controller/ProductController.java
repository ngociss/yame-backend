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
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ProductStatus;
import vn.yame.dto.reponse.ProductResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.ProductRequest;
import vn.yame.dto.request.UpdateStatusRequest;
import vn.yame.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/products")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Product Management", description = "APIs for managing products")
public class ProductController {

    private final ProductService productService;

    @PostMapping
    @Operation(
        summary = "Create a new product",
        description = "Create a new product with name, slug, prices, category and material"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or product already exists"),
        @ApiResponse(responseCode = "404", description = "Category or Material not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductResponse>> createProduct(
            @Valid @RequestBody ProductRequest request) {
        log.info("REST request to create product: {}", request.getName());
        ProductResponse response = productService.createProduct(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Product created successfully",
                response
            ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing product",
        description = "Update product information by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product, Category or Material not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductResponse>> updateProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductRequest request) {
        log.info("REST request to update product with id: {}", id);

        ProductResponse response = productService.updateProduct(id, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product updated successfully",
            response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a product",
        description = "Soft delete a product by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteProduct(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete product with id: {}", id);

        productService.deleteProduct(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product deleted successfully",
            null
        ));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get product by ID",
        description = "Retrieve product details by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductResponse>> getProductById(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get product with id: {}", id);

        ProductResponse response = productService.getProductById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product retrieved successfully",
            response
        ));
    }

    @GetMapping("/slug/{slug}")
    @Operation(
        summary = "Get product by slug",
        description = "Retrieve product details by slug"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductResponse>> getProductBySlug(
            @Parameter(description = "Product slug", required = true)
            @PathVariable String slug) {
        log.info("REST request to get product with slug: {}", slug);

        ProductResponse response = productService.getProductBySlug(slug);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product retrieved successfully",
            response
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all products",
        description = "Retrieve all products without pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductResponse>>> getAllProducts() {
        log.info("REST request to get all products");

        List<ProductResponse> response = productService.getAllProducts();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Products retrieved successfully",
            response
        ));
    }

    @GetMapping("/pagination")
    @Operation(
        summary = "Get products with pagination",
        description = "Retrieve products with pagination and sorting"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<ProductResponse>>> getAllProductsWithPagination(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get products with pagination - page: {}, size: {}, sortBy: {}, direction: {}",
            page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ProductResponse> response = productService.getAllProductsWithPagination(pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Products retrieved successfully",
            response
        ));
    }

    @GetMapping("/category/{categoryId}")
    @Operation(
        summary = "Get products by category",
        description = "Retrieve products by category ID with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<ProductResponse>>> getProductsByCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long categoryId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get products by category: {}", categoryId);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ProductResponse> response = productService.getProductsByCategory(categoryId, pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Products retrieved successfully",
            response
        ));
    }

    @GetMapping("/material/{materialId}")
    @Operation(
        summary = "Get products by material",
        description = "Retrieve products by material ID with pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Material not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<ProductResponse>>> getProductsByMaterial(
            @Parameter(description = "Material ID", required = true)
            @PathVariable Long materialId,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get products by material: {}", materialId);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ProductResponse> response = productService.getProductsByMaterial(materialId, pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Products retrieved successfully",
            response
        ));
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search products",
        description = "Search products with multiple filters: category, material, status, product status, name"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Products retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<ProductResponse>>> searchProducts(
            @Parameter(description = "Category ID")
            @RequestParam(required = false) Long categoryId,
            @Parameter(description = "Material ID")
            @RequestParam(required = false) Long materialId,
            @Parameter(description = "Common Status (ACTIVE, INACTIVE, DELETED)")
            @RequestParam(required = false) CommonStatus status,
            @Parameter(description = "Product Status (ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED)")
            @RequestParam(required = false) ProductStatus productStatus,
            @Parameter(description = "Product name (partial match)")
            @RequestParam(required = false) String name,
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to search products with filters");

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ProductResponse> response = productService.searchProducts(
            categoryId, materialId, status, productStatus, name, pageable
        );

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Products retrieved successfully",
            response
        ));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get all active products",
        description = "Retrieve all products with ACTIVE status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active products retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductResponse>>> getActiveProducts() {
        log.info("REST request to get all active products");

        List<ProductResponse> response = productService.getActiveProducts();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Active products retrieved successfully",
            response
        ));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update product status",
        description = "Update the common status of a product (ACTIVE, INACTIVE, DELETED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductResponse>> updateProductStatus(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("REST request to update product status - id: {}, status: {}", id, request.getStatus());

        ProductResponse response = productService.updateStatus(id, request.getStatus());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product status updated successfully",
            response
        ));
    }

    @PatchMapping("/{id}/product-status")
    @Operation(
        summary = "Update product availability status",
        description = "Update the product status (ACTIVE, INACTIVE, OUT_OF_STOCK, DISCONTINUED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductResponse>> updateProductAvailabilityStatus(
            @Parameter(description = "Product ID", required = true)
            @PathVariable Long id,
            @Parameter(description = "Product Status", required = true)
            @RequestParam ProductStatus productStatus) {
        log.info("REST request to update product availability status - id: {}, productStatus: {}", id, productStatus);

        ProductResponse response = productService.updateProductStatus(id, productStatus);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product status updated successfully",
            response
        ));
    }
}

