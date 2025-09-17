package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yame.dto.reponse.AddressResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.AddressRequest;
import vn.yame.service.AddressService;

import static vn.yame.dto.reponse.ResponseData.success;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AddressController {

    private final AddressService addressService;


    @PostMapping("/addresses")
    @Operation(summary = "Create a new address", description = "Create a new address with the provided details")
    public ResponseEntity<ResponseData<AddressResponse>> createAddress(@RequestBody AddressRequest addressRequest) {
        AddressResponse addressResponse = addressService.createAddress(addressRequest);
        return ResponseEntity.status(HttpStatus.CREATED).body(ResponseData.
                success(HttpStatus.CREATED.value(), true, "Address created successfully", addressResponse));
    }

    @PatchMapping("/addresses/default/{id}")
    @Operation(summary = "Set Default", description = "Set an address as the default address")
    public ResponseEntity<ResponseData<AddressResponse>> setDefaultAddress(@PathVariable Long id, @RequestParam Long userId) {
        addressService.setDefaultAddress(id, userId);
        return ResponseEntity.ok(success(HttpStatus.OK.value(), true, "Set default address successfully", null));
    }


}
