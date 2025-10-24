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
import vn.yame.dto.reponse.SizeResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.SizeRequest;
import vn.yame.dto.request.UpdateStatusRequest;
import vn.yame.service.SizeService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/sizes")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Size Management", description = "APIs for managing product sizes")
public class SizeController {

    private final SizeService sizeService;

    @PostMapping
    @Operation(
        summary = "Create a new size",
        description = "Create a new product size with name and description"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Size created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or size already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<SizeResponse>> createSize(
            @Valid @RequestBody SizeRequest request) {
        log.info("REST request to create size: {}", request.getName());
        SizeResponse response = sizeService.createSize(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Size created successfully",
                response
            ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing size",
        description = "Update size information by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Size updated successfully"),
        @ApiResponse(responseCode = "404", description = "Size not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<SizeResponse>> updateSize(
            @Parameter(description = "Size ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody SizeRequest request) {
        log.info("REST request to update size with id: {}", id);

        SizeResponse response = sizeService.updateSize(id, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Size updated successfully",
            response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a size",
        description = "Soft delete a size by ID. Cannot delete if size is being used by product variants."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Size deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Size not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete size with product variants"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteSize(
            @Parameter(description = "Size ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete size with id: {}", id);

        sizeService.deleteSize(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Size deleted successfully",
            null
        ));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get size by ID",
        description = "Retrieve size details by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Size retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Size not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<SizeResponse>> getSizeById(
            @Parameter(description = "Size ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get size with id: {}", id);

        SizeResponse response = sizeService.getSizeById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Size retrieved successfully",
            response
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all sizes",
        description = "Retrieve all sizes without pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sizes retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<SizeResponse>>> getAllSizes() {
        log.info("REST request to get all sizes");

        List<SizeResponse> response = sizeService.getAllSizes();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Sizes retrieved successfully",
            response
        ));
    }

    @GetMapping("/pagination")
    @Operation(
        summary = "Get sizes with pagination",
        description = "Retrieve sizes with pagination and sorting"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Sizes retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<SizeResponse>>> getAllSizesWithPagination(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get sizes with pagination - page: {}, size: {}, sortBy: {}, direction: {}",
            page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<SizeResponse> response = sizeService.getAllSizesWithPagination(pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Sizes retrieved successfully",
            response
        ));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get all active sizes",
        description = "Retrieve all sizes with ACTIVE status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active sizes retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<SizeResponse>>> getActiveSizes() {
        log.info("REST request to get all active sizes");

        List<SizeResponse> response = sizeService.getActiveSizes();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Active sizes retrieved successfully",
            response
        ));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update size status",
        description = "Update the status of a size (ACTIVE, INACTIVE, DELETED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Size status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Size not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<SizeResponse>> updateSizeStatus(
            @Parameter(description = "Size ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("REST request to update size status - id: {}, status: {}", id, request.getStatus());

        SizeResponse response = sizeService.updateStatus(id, request.getStatus());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Size status updated successfully",
            response
        ));
    }
}

