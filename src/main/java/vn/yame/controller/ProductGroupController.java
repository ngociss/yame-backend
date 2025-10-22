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
import vn.yame.dto.reponse.ProductGroupResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.ProductGroupRequest;
import vn.yame.service.ProductGroupService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/product-groups")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Product Group Management", description = "APIs for managing product groups")
public class ProductGroupController {

    private final ProductGroupService productGroupService;

    @PostMapping
    @Operation(
        summary = "Create a new product group",
        description = "Create a new product group with name and description"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Product group created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or product group already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductGroupResponse>> createProductGroup(
            @Valid @RequestBody ProductGroupRequest request) {
        log.info("REST request to create product group: {}", request.getName());
        ProductGroupResponse response = productGroupService.createProductGroup(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Product group created successfully",
                response
            ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing product group",
        description = "Update product group information by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product group updated successfully"),
        @ApiResponse(responseCode = "404", description = "Product group not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductGroupResponse>> updateProductGroup(
            @Parameter(description = "Product Group ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ProductGroupRequest request) {
        log.info("REST request to update product group with id: {}", id);

        ProductGroupResponse response = productGroupService.updateProductGroup(id, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product group updated successfully",
            response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a product group",
        description = "Delete a product group by ID. Cannot delete if product group has products."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product group deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Product group not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete product group with products"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteProductGroup(
            @Parameter(description = "Product Group ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete product group with id: {}", id);

        productGroupService.deleteProductGroup(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product group deleted successfully",
            null
        ));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get product group by ID",
        description = "Retrieve product group details by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product group retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Product group not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ProductGroupResponse>> getProductGroupById(
            @Parameter(description = "Product Group ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get product group with id: {}", id);

        ProductGroupResponse response = productGroupService.getProductGroupById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product group retrieved successfully",
            response
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all product groups",
        description = "Retrieve all product groups without pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product groups retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductGroupResponse>>> getAllProductGroups() {
        log.info("REST request to get all product groups");

        List<ProductGroupResponse> response = productGroupService.getAllProductGroups();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product groups retrieved successfully",
            response
        ));
    }

    @GetMapping("/pagination")
    @Operation(
        summary = "Get product groups with pagination",
        description = "Retrieve product groups with pagination and sorting"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product groups retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<ProductGroupResponse>>> getAllProductGroupsWithPagination(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get product groups with pagination - page: {}, size: {}, sortBy: {}, direction: {}",
            page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ProductGroupResponse> response = productGroupService.getAllProductGroupsWithPagination(pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product groups retrieved successfully",
            response
        ));
    }

    @GetMapping("/search")
    @Operation(
        summary = "Search product groups by name",
        description = "Search product groups by name (case-insensitive partial match)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Product groups retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ProductGroupResponse>>> searchProductGroups(
            @Parameter(description = "Product group name to search", required = true)
            @RequestParam String name) {
        log.info("REST request to search product groups by name: {}", name);

        List<ProductGroupResponse> response = productGroupService.searchProductGroupsByName(name);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Product groups retrieved successfully",
            response
        ));
    }
}

