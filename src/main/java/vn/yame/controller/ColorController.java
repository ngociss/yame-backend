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
import vn.yame.dto.reponse.ColorResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.ColorRequest;
import vn.yame.dto.request.UpdateStatusRequest;
import vn.yame.service.ColorService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/colors")
@RequiredArgsConstructor
@Validated
@Slf4j
@Tag(name = "Color Management", description = "APIs for managing product colors")
public class ColorController {

    private final ColorService colorService;

    @PostMapping
    @Operation(
        summary = "Create a new color",
        description = "Create a new product color with name, hex code and description"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Color created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or color already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ColorResponse>> createColor(
            @Valid @RequestBody ColorRequest request) {
        log.info("REST request to create color: {}", request.getName());
        ColorResponse response = colorService.createColor(request);

        return ResponseEntity
            .status(HttpStatus.CREATED)
            .body(ResponseData.success(
                HttpStatus.CREATED.value(),
                true,
                "Color created successfully",
                response
            ));
    }

    @PutMapping("/{id}")
    @Operation(
        summary = "Update an existing color",
        description = "Update color information by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Color updated successfully"),
        @ApiResponse(responseCode = "404", description = "Color not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ColorResponse>> updateColor(
            @Parameter(description = "Color ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody ColorRequest request) {
        log.info("REST request to update color with id: {}", id);

        ColorResponse response = colorService.updateColor(id, request);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Color updated successfully",
            response
        ));
    }

    @DeleteMapping("/{id}")
    @Operation(
        summary = "Delete a color",
        description = "Soft delete a color by ID. Cannot delete if color is being used by product variants."
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Color deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Color not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete color with product variants"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Void>> deleteColor(
            @Parameter(description = "Color ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to delete color with id: {}", id);

        colorService.deleteColor(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Color deleted successfully",
            null
        ));
    }

    @GetMapping("/{id}")
    @Operation(
        summary = "Get color by ID",
        description = "Retrieve color details by ID"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Color retrieved successfully"),
        @ApiResponse(responseCode = "404", description = "Color not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ColorResponse>> getColorById(
            @Parameter(description = "Color ID", required = true)
            @PathVariable Long id) {
        log.info("REST request to get color with id: {}", id);

        ColorResponse response = colorService.getColorById(id);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Color retrieved successfully",
            response
        ));
    }

    @GetMapping
    @Operation(
        summary = "Get all colors",
        description = "Retrieve all colors without pagination"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Colors retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ColorResponse>>> getAllColors() {
        log.info("REST request to get all colors");

        List<ColorResponse> response = colorService.getAllColors();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Colors retrieved successfully",
            response
        ));
    }

    @GetMapping("/pagination")
    @Operation(
        summary = "Get colors with pagination",
        description = "Retrieve colors with pagination and sorting"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Colors retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<Page<ColorResponse>>> getAllColorsWithPagination(
            @Parameter(description = "Page number (0-based)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {
        log.info("REST request to get colors with pagination - page: {}, size: {}, sortBy: {}, direction: {}",
            page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc") ? Sort.Direction.ASC : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ColorResponse> response = colorService.getAllColorsWithPagination(pageable);

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Colors retrieved successfully",
            response
        ));
    }

    @GetMapping("/active")
    @Operation(
        summary = "Get all active colors",
        description = "Retrieve all colors with ACTIVE status"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Active colors retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<List<ColorResponse>>> getActiveColors() {
        log.info("REST request to get all active colors");

        List<ColorResponse> response = colorService.getActiveColors();

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Active colors retrieved successfully",
            response
        ));
    }

    @PatchMapping("/{id}/status")
    @Operation(
        summary = "Update color status",
        description = "Update the status of a color (ACTIVE, INACTIVE, DELETED)"
    )
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Color status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Color not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
    public ResponseEntity<ResponseData<ColorResponse>> updateColorStatus(
            @Parameter(description = "Color ID", required = true)
            @PathVariable Long id,
            @Valid @RequestBody UpdateStatusRequest request) {
        log.info("REST request to update color status - id: {}, status: {}", id, request.getStatus());

        ColorResponse response = colorService.updateStatus(id, request.getStatus());

        return ResponseEntity.ok(ResponseData.success(
            HttpStatus.OK.value(),
            true,
            "Color status updated successfully",
            response
        ));
    }
}

