package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yame.dto.reponse.AddressResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.AddressRequest;
import vn.yame.service.AddressService;

import java.util.List;

import static vn.yame.dto.reponse.ResponseData.success;

@RestController
@RequestMapping("/api/v1/addresses")
@RequiredArgsConstructor
@Tag(name = "Address Management", description = "APIs for managing user addresses")
public class AddressController {

    private final AddressService addressService;

    @PostMapping
    @Operation(summary = "Create a new address", description = "Create a new address with street, ward, and province")
    public ResponseEntity<ResponseData<AddressResponse>> createAddress(
            @Valid @RequestBody AddressRequest addressRequest) {
        AddressResponse addressResponse = addressService.createAddress(addressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                success(HttpStatus.CREATED.value(), true, "Address created successfully", addressResponse));
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an address", description = "Update an existing address")
    public ResponseEntity<ResponseData<AddressResponse>> updateAddress(
            @PathVariable Long id,
            @Valid @RequestBody AddressRequest addressRequest) {
        AddressResponse addressResponse = addressService.updateAddress(id, addressRequest);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Address updated successfully", addressResponse));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an address", description = "Delete an address by ID")
    public ResponseEntity<ResponseData<Void>> deleteAddress(@PathVariable Long id) {
        addressService.deleteAddress(id);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Address deleted successfully", null));
    }

    @GetMapping("/user/{userId}")
    @Operation(summary = "Get all addresses", description = "Get all addresses for a user")
    public ResponseEntity<ResponseData<List<AddressResponse>>> getAddressesByUser(
            @PathVariable Long userId) {
        List<AddressResponse> addresses = addressService.getAddressesByUser(userId);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Addresses retrieved successfully", addresses));
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get address by ID", description = "Get a specific address by ID")
    public ResponseEntity<ResponseData<AddressResponse>> getAddressById(@PathVariable Long id) {
        AddressResponse addressResponse = addressService.getAddressById(id);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Address retrieved successfully", addressResponse));
    }

    @PatchMapping("/{id}/default")
    @Operation(summary = "Set default address", description = "Set an address as the default address for a user")
    public ResponseEntity<ResponseData<AddressResponse>> setDefaultAddress(
            @PathVariable Long id,
            @RequestParam Long userId) {
        AddressResponse addressResponse = addressService.setDefaultAddress(id, userId);
        return ResponseEntity.ok(
                success(HttpStatus.OK.value(), true, "Default address set successfully", addressResponse));
    }
}
