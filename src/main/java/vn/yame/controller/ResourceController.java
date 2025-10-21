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
import org.springframework.web.bind.annotation.*;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.reponse.ResourceResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.ResourceRequest;
import vn.yame.service.ResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Resource Management", description = "APIs for managing system resources")
public class ResourceController {

    private final ResourceService resourceService;

    @GetMapping
    @Operation(summary = "Get all resources", description = "Retrieve all system resources")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resources retrieved successfully"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
//    @PreAuthorize("hasAuthority('RESOURCE_READ')")
    public ResponseEntity<ResponseData<List<ResourceResponse>>> getAllResourcesList() {
        log.info("REST request to get all resources");
        List<ResourceResponse> resources = resourceService.getAllResources();
        return ResponseEntity.ok(
                ResponseData.success(HttpStatus.OK.value(), true, "Fetched all resources successfully", resources)
        );
    }

    @GetMapping("/page")
    @Operation(summary = "Get resources with pagination", description = "Retrieve resources with pagination and sorting")
    public ResponseEntity<ResponseData<Page<ResourceResponse>>> getResourcesWithPagination(
            @Parameter(description = "Page number (0-indexed)")
            @RequestParam(defaultValue = "0") int page,
            @Parameter(description = "Page size")
            @RequestParam(defaultValue = "10") int size,
            @Parameter(description = "Sort by field")
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @Parameter(description = "Sort direction (asc/desc)")
            @RequestParam(defaultValue = "desc") String direction) {

        log.info("REST request to get resources - page: {}, size: {}, sortBy: {}, direction: {}",
                 page, size, sortBy, direction);

        Sort.Direction sortDirection = direction.equalsIgnoreCase("asc")
            ? Sort.Direction.ASC
            : Sort.Direction.DESC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(sortDirection, sortBy));

        Page<ResourceResponse> resources = resourceService.getAllResourcesWithPagination(pageable);

        return ResponseEntity.ok(
            ResponseData.success(HttpStatus.OK.value(), true, "Resources retrieved successfully", resources)
        );
    }

    @GetMapping("/active")
    @Operation(summary = "Get active resources", description = "Retrieve only active resources")
    public ResponseEntity<ResponseData<List<ResourceResponse>>> getActiveResources() {
        log.info("REST request to get active resources");
        List<ResourceResponse> resources = resourceService.getActiveResources();
        return ResponseEntity.ok(
            ResponseData.success(HttpStatus.OK.value(), true, "Active resources retrieved successfully", resources)
        );
    }

    @GetMapping("/status/{status}")
    @Operation(summary = "Get resources by status", description = "Retrieve resources filtered by status")
    public ResponseEntity<ResponseData<List<ResourceResponse>>> getResourcesByStatus(
            @Parameter(description = "Resource status") @PathVariable CommonStatus status) {
        log.info("REST request to get resources with status: {}", status);
        List<ResourceResponse> resources = resourceService.getResourcesByStatus(status);
        return ResponseEntity.ok(
            ResponseData.success(HttpStatus.OK.value(), true, "Resources retrieved successfully", resources)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID", description = "Retrieve a specific resource by its ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resource found"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
//    @PreAuthorize("hasAuthority('RESOURCE_READ')")
    public ResponseEntity<ResponseData<ResourceResponse>> getResourceById(
            @Parameter(description = "Resource ID", required = true) @PathVariable Long id) {
        log.info("REST request to get resource with id: {}", id);
        ResourceResponse resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(
                ResponseData.success(HttpStatus.OK.value(), true, "Fetched resource successfully", resource)
        );
    }

    @GetMapping("/name/{name}")
    @Operation(summary = "Get resource by name", description = "Retrieve a specific resource by its name")
    public ResponseEntity<ResponseData<ResourceResponse>> getResourceByName(
            @Parameter(description = "Resource name", required = true) @PathVariable String name) {
        log.info("REST request to get resource with name: {}", name);
        ResourceResponse resource = resourceService.getResourceByName(name);
        return ResponseEntity.ok(
            ResponseData.success(HttpStatus.OK.value(), true, "Resource retrieved successfully", resource)
        );
    }

    @PostMapping
    @Operation(summary = "Create a new resource", description = "Create a new system resource")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "201", description = "Resource created successfully"),
        @ApiResponse(responseCode = "400", description = "Invalid input or resource already exists"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
//    @PreAuthorize("hasAuthority('RESOURCE_CREATE')")
    public ResponseEntity<ResponseData<ResourceResponse>> createResource(
            @Valid @RequestBody ResourceRequest request) {
        log.info("REST request to create resource: {}", request.getName());
        ResourceResponse createdResource = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseData.success(HttpStatus.CREATED.value(), true, "Resource created successfully", createdResource)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing resource", description = "Update resource information by ID")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resource updated successfully"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "400", description = "Invalid input"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
//    @PreAuthorize("hasAuthority('RESOURCE_UPDATE')")
    public ResponseEntity<ResponseData<ResourceResponse>> updateResource(
            @Parameter(description = "Resource ID", required = true) @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {
        log.info("REST request to update resource with id: {}", id);
        ResourceResponse updatedResource = resourceService.updateResource(id, request);
        return ResponseEntity.ok(
                ResponseData.success(HttpStatus.OK.value(), true, "Resource updated successfully", updatedResource)
        );
    }

    @PatchMapping("/{id}/status")
    @Operation(summary = "Update resource status", description = "Update the status of a resource (ACTIVE/INACTIVE/DELETED)")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resource status updated successfully"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "400", description = "Invalid status value")
    })
//    @PreAuthorize("hasAuthority('RESOURCE_UPDATE')")
    public ResponseEntity<ResponseData<ResourceResponse>> updateResourceStatus(
            @Parameter(description = "Resource ID", required = true) @PathVariable Long id,
            @Valid @RequestBody vn.yame.dto.request.UpdateStatusRequest request) {
        log.info("REST request to update status for resource id: {} to {}", id, request.getStatus());
        ResourceResponse updatedResource = resourceService.updateStatus(id, request.getStatus());
        return ResponseEntity.ok(
                ResponseData.success(HttpStatus.OK.value(), true, "Resource status updated successfully", updatedResource)
        );
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a resource", description = "Soft delete a resource by ID. Cannot delete if resource has permissions.")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Resource deleted successfully"),
        @ApiResponse(responseCode = "404", description = "Resource not found"),
        @ApiResponse(responseCode = "400", description = "Cannot delete resource with permissions"),
        @ApiResponse(responseCode = "500", description = "Internal server error")
    })
//    @PreAuthorize("hasAuthority('RESOURCE_DELETE')")
    public ResponseEntity<ResponseData<Void>> deleteResource(
            @Parameter(description = "Resource ID", required = true) @PathVariable Long id) {
        log.info("REST request to soft delete resource with id: {}", id);
        resourceService.deleteResource(id);
        return ResponseEntity.ok(
                ResponseData.success(HttpStatus.OK.value(), true, "Resource deleted successfully", null)
        );
    }
}
