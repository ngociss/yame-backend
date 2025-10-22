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
import vn.yame.dto.reponse.MaterialResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.MaterialRequest;
import vn.yame.dto.request.UpdateStatusRequest;
import vn.yame.service.MaterialService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/materials")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Material Management", description = "APIs for managing product materials")
public class MaterialController {

    private final MaterialService materialService;

    @PostMapping
    @Operation(
        summary = "Create a new material",
        description = "Create a new product material with name and description"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Material created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or material already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<MaterialResponse>> createMaterial(
            @Valid @RequestBody MaterialRequest request) {
        log.info("REST request to create material: {}", request.getName());
        MaterialResponse response = materialService.createMaterial(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Material created successfully",
                response
            ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing material",
        description = "Update material information by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Material updated successfully"),
        @ApiResponse(responseCode = "404", description = "Material not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<MaterialResponse>> updateMaterial(
            @Parameter(description = "Material ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody MaterialRequest request) {
        log.info("REST request to update material with id: {}", id);

        MaterialResponse response = materialService.updateMaterial(id, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Material updated successfully",
            response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a material",
        description = "Soft delete a material by ID. Cannot delete if material is being used by products."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Material deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Material not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete material with products"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteMaterial(
            @Parameter(description = "Material ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete material with id: {}", id);

        materialService.deleteMaterial(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Material deleted successfully",
            null
        ));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get material by ID",
        description = "Retrieve material details by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Material retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Material not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<MaterialResponse>> getMaterialById(
            @Parameter(description = "Material ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get material with id: {}", id);

        MaterialResponse response = materialService.getMaterialById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Material retrieved successfully",
            response
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all materials",
        description = "Retrieve all materials without pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Materials retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<MaterialResponse>>> getAllMaterials() {
        log.info("REST request to get all materials");

        List<MaterialResponse> response = materialService.getAllMaterials();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Materials retrieved successfully",
            response
        ));
    }

    @GetMapping("/pagination")
    @Operation(
        summary = "Get materials with pagination",
        description = "Retrieve materials with pagination and sorting"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Materials retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<MaterialResponse>>> getAllMaterialsWithPagination(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get materials with pagination - page: {}, size: {}, sortBy: {}, direction: {}",
            page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<MaterialResponse> response = materialService.getAllMaterialsWithPagination(pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Materials retrieved successfully",
            response
        ));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get all active materials",
        description = "Retrieve all materials with ACTIVE status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active materials retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<MaterialResponse>>> getActiveMaterials() {
        log.info("REST request to get all active materials");

        List<MaterialResponse> response = materialService.getActiveMaterials();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Active materials retrieved successfully",
            response
        ));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update material status",
        description = "Update the status of a material (ACTIVE, INACTIVE, DELETED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Material status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Material not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<MaterialResponse>> updateMaterialStatus(
            @Parameter(description = "Material ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("REST request to update material status - id: {}, status: {}", id, request.getStatus());

        MaterialResponse response = materialService.updateStatus(id, request.getStatus());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Material status updated successfully",
            response
        ));
    }
}

