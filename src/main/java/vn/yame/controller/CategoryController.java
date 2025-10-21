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
import vn.yame.dto.reponse.CategoryResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.CategoryRequest;
import vn.yame.service.CategoryService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Category Management", description = "APIs for managing product categories")
public class CategoryController {

    private final CategoryService categoryService;

    @PostMapping
    @Operation(
        summary = "Create a new category",
        description = "Create a new product category with name, slug, and description"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Category created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or category already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<CategoryResponse>> createCategory(
            @Valid @RequestBody CategoryRequest request) {
        log.info("REST request to create category: {}", request.getName());
        CategoryResponse response = categoryService.createCategory(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Category created successfully",
                response
            ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing category",
        description = "Update category information by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<CategoryResponse>> updateCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody CategoryRequest request) {
        log.info("REST request to update category with id: {}", id);

        CategoryResponse response = categoryService.updateCategory(id, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Category updated successfully",
            response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a category",
        description = "Delete a category by ID. Cannot delete if category has products."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete category with products"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteCategory(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete category with id: {}", id);

        categoryService.deleteCategory(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Category deleted successfully",
            null
        ));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get category by ID",
        description = "Retrieve a single category by its ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<CategoryResponse>> getCategoryById(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get category with id: {}", id);

        CategoryResponse response = categoryService.getCategoryById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Category retrieved successfully",
            response
        ));
    }

    @GetMapping("/slug/{slug}")
    @Operation(
        summary = "Get category by slug",
        description = "Retrieve a single category by its slug"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category found"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<CategoryResponse>> getCategoryBySlug(
            @Parameter(description = "Category slug", required = true)
            @PathVariable String slug) {
        log.info("REST request to get category with slug: {}", slug);

        CategoryResponse response = categoryService.getCategoryBySlug(slug);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Category retrieved successfully",
            response
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all categories",
        description = "Retrieve all categories without pagination"
    )
    public ResponseEntity<ResponseData<List<CategoryResponse>>> getAllCategories() {
        log.info("REST request to get all categories");

        List<CategoryResponse> response = categoryService.getAllCategories();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Categories retrieved successfully",
            response
        ));
    }

    @GetMapping("/page")
    @Operation(
        summary = "Get categories with pagination",
        description = "Retrieve categories with pagination and sorting support"
    )
    public ResponseEntity<ResponseData<Page<CategoryResponse>>> getCategoriesWithPagination(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get categories - page: {}, size: {}, sortBy: {}, direction: {}",
                 page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<CategoryResponse> response = categoryService.getAllCategoriesWithPagination(pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Categories retrieved successfully",
            response
        ));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get all active categories",
        description = "Retrieve only active categories"
    )
    public ResponseEntity<ResponseData<List<CategoryResponse>>> getActiveCategories() {
        log.info("REST request to get active categories");

        List<CategoryResponse> response = categoryService.getActiveCategories();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Active categories retrieved successfully",
            response
        ));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update category status",
        description = "Update the status of a category (ACTIVE/INACTIVE)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Category status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Category not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
    public ResponseEntity<ResponseData<CategoryResponse>> updateCategoryStatus(
            @Parameter(description = "Category ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody vn.yame.dto.request.UpdateStatusRequest request) {
        log.info("REST request to update status for category id: {} to {}", id, request.getStatus());

        CategoryResponse response = categoryService.updateStatus(id, request.getStatus());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Category status updated successfully",
            response
        ));
    }
}
