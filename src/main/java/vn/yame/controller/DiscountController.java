package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.reponse.DiscountResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.DiscountRequest;
import vn.yame.dto.request.UpdateStatusRequest;
import vn.yame.service.DiscountService;

import java.util.List;

import static vn.yame.dto.reponse.ResponseData.success;

@RestController
@RequestMapping("/api/v1/discounts")
@RequiredArgsConstructor
@Tag(name = "Discount Management", description = "APIs for managing discount codes and promotions")
public class DiscountController {

    private final DiscountService discountService;

    @PostMapping
    @Operation(summary = "Create a new discount", description = "Create a new discount code with specified rules")
    public ResponseEntity<ResponseData<DiscountResponse>> createDiscount(
            @Valid @RequestBody DiscountRequest request) {
        DiscountResponse response = discountService.createDiscount(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                success(HttpStatus.CREATED.value(), true, "Discount created successfully", response));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update a discount", description = "Update an existing discount by ID")
    public ResponseEntity<ResponseData<DiscountResponse>> updateDiscount(
            @PathVariable Long id,
            @Valid @RequestBody DiscountRequest request) {
        DiscountResponse response = discountService.updateDiscount(id, request);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Discount updated successfully", response));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a discount", description = "Delete a discount by ID")
    public ResponseEntity<ResponseData<Void>> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Discount deleted successfully", null));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get discount by ID", description = "Retrieve a specific discount by ID")
    public ResponseEntity<ResponseData<DiscountResponse>> getDiscountById(@PathVariable Long id) {
        DiscountResponse response = discountService.getDiscountById(id);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Discount retrieved successfully", response));
    }

    @GetMapping("/code/{code}")
    @Operation(summary = "Get discount by code", description = "Retrieve a discount by its code")
    public ResponseEntity<ResponseData<DiscountResponse>> getDiscountByCode(@PathVariable String code) {
        DiscountResponse response = discountService.getDiscountByCode(code);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Discount retrieved successfully", response));
    }

    @GetMapping
    @Operation(summary = "Get all discounts", description = "Retrieve all discounts with pagination")
    public ResponseEntity<ResponseData<Page<DiscountResponse>>> getAllDiscounts(
            @Parameter(description = "Page number (0-indexed)") @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size") @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field") @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction") @RequestParam(defaultValue = "DESC") String direction) {

        Sort.Direction sortDirection = Sort.Direction.fromString(direction);
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<DiscountResponse> discounts = discountService.getAllDiscounts(pageable);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Discounts retrieved successfully", discounts));
    }

    @GetMapping("/active")
    @Operation(summary = "Get active discounts", description = "Retrieve all currently active and valid discounts")
    public ResponseEntity<ResponseData<List<DiscountResponse>>> getActiveDiscounts() {
        List<DiscountResponse> discounts = discountService.getActiveDiscounts();
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Active discounts retrieved successfully", discounts));
    }

    @PostMapping("/validate")
    @Operation(summary = "Validate discount code", description = "Validate a discount code for a specific order amount")
    public ResponseEntity<ResponseData<DiscountResponse>> validateDiscount(
            @RequestParam String code,
            @RequestParam Double orderAmount) {
        DiscountResponse response = discountService.validateAndGetDiscount(code, orderAmount);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Discount is valid", response));
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update discount status", description = "Update the status of a discount (ACTIVE/INACTIVE)")
    public ResponseEntity<ResponseData<DiscountResponse>> updateDiscountStatus(
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        DiscountResponse response = discountService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Discount status updated successfully", response));
    }
}

