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
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.reponse.ShippingMethodResponse;
import vn.yame.dto.request.ShippingMethodRequest;
import vn.yame.dto.request.UpdateStatusRequest;
import vn.yame.service.ShippingMethodService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/shipping-methods")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Shipping Method Management", description = "APIs for managing shipping methods")
public class ShippingMethodController {

    private final ShippingMethodService shippingMethodService;

    @PostMapping
    @Operation(
        summary = "Create a new shipping method",
        description = "Create a new shipping method with corporation name, cost and estimated delivery days. ADMIN only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Shipping method created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or shipping method already exists"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ShippingMethodResponse>> createShippingMethod(
            @Valid @RequestBody ShippingMethodRequest request) {
        log.info("REST request to create shipping method: {}", request.getCorpName());

        ShippingMethodResponse response = shippingMethodService.createShippingMethod(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Shipping method created successfully",
                response
            ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing shipping method",
        description = "Update shipping method information by ID. ADMIN only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipping method updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
        @ApiResponse(responseCode = "404", description = "Shipping method not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ShippingMethodResponse>> updateShippingMethod(
            @Parameter(description = "Shipping method ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ShippingMethodRequest request) {
        log.info("REST request to update shipping method with id: {}", id);

        ShippingMethodResponse response = shippingMethodService.updateShippingMethod(id, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Shipping method updated successfully",
            response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a shipping method",
        description = "Soft delete a shipping method by setting status to INACTIVE. ADMIN only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipping method deleted successfully"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
        @ApiResponse(responseCode = "404", description = "Shipping method not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteShippingMethod(
            @Parameter(description = "Shipping method ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete shipping method with id: {}", id);

        shippingMethodService.deleteShippingMethod(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Shipping method deleted successfully",
            null
        ));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get shipping method by ID",
        description = "Retrieve shipping method details by ID. Public access."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipping method retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Shipping method not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ShippingMethodResponse>> getShippingMethodById(
            @Parameter(description = "Shipping method ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get shipping method with id: {}", id);

        ShippingMethodResponse response = shippingMethodService.getShippingMethodById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Shipping method retrieved successfully",
            response
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all shipping methods with pagination",
        description = "Retrieve all shipping methods with pagination and sorting. Public access."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipping methods retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<ShippingMethodResponse>>> getAllShippingMethods(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get all shipping methods - page: {}, size: {}, sortBy: {}, direction: {}",
            page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ShippingMethodResponse> response = shippingMethodService.getAllShippingMethods(pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Shipping methods retrieved successfully",
            response
        ));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get all active shipping methods",
        description = "Retrieve all shipping methods with ACTIVE status. Used during checkout. Public access."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active shipping methods retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ShippingMethodResponse>>> getActiveShippingMethods() {
        log.info("REST request to get all active shipping methods");

        List<ShippingMethodResponse> response = shippingMethodService.getActiveShippingMethods();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Active shipping methods retrieved successfully",
            response
        ));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update shipping method status",
        description = "Update the status of a shipping method (ACTIVE or INACTIVE). ADMIN only."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Shipping method status updated successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid status value"),
        @ApiResponse(responseCode = "401", description = "Unauthorized"),
        @ApiResponse(responseCode = "403", description = "Forbidden - Admin only"),
        @ApiResponse(responseCode = "404", description = "Shipping method not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ShippingMethodResponse>> updateShippingMethodStatus(
            @Parameter(description = "Shipping method ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("REST request to update shipping method status - id: {}, status: {}", id, request.getStatus());

        ShippingMethodResponse response = shippingMethodService.updateStatus(id, request.getStatus());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Shipping method status updated successfully",
            response
        ));
    }
}

