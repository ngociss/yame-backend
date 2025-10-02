package vn.yame.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.yame.dto.reponse.ResourceResponse;
import vn.yame.dto.reponse.ResponseData;
import vn.yame.dto.request.ResourceRequest;
import vn.yame.service.ResourceService;

import java.util.List;

@RestController
@RequestMapping("/api/v1/resources")
@RequiredArgsConstructor
@Tag(name = "Resource Management", description = "APIs for managing system resources")
public class ResourceController {

    private final ResourceService resourceService;


    @GetMapping("")
    @Operation(summary = "Get all resources")
//    @PreAuthorize("hasAuthority('RESOURCE_READ')")
    public ResponseEntity<ResponseData<List<ResourceResponse>>> getAllResourcesList() {
        List<ResourceResponse> resources = resourceService.getAllResources();
        return ResponseEntity.ok(
                ResponseData.success(HttpStatus.OK.value(), true, "Fetched all resources successfully", resources)
        );
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get resource by ID")
//    @PreAuthorize("hasAuthority('RESOURCE_READ')")
    public ResponseEntity<ResponseData<ResourceResponse>> getResourceById(
            @Parameter(description = "Resource ID") @PathVariable Long id) {
        ResourceResponse resource = resourceService.getResourceById(id);
        return ResponseEntity.ok(
                ResponseData.success(HttpStatus.OK.value(), true, "Fetched resource successfully", resource)
        );
    }


    @PostMapping
    @Operation(summary = "Create a new resource")
//    @PreAuthorize("hasAuthority('RESOURCE_CREATE')")
    public ResponseEntity<ResponseData<ResourceResponse>> createResource(
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse createdResource = resourceService.createResource(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(
                ResponseData.success(HttpStatus.CREATED.value(), true, "Resource created successfully", createdResource)
        );
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update an existing resource")
//    @PreAuthorize("hasAuthority('RESOURCE_UPDATE')")
    public ResponseEntity<ResponseData<ResourceResponse>> updateResource(
            @Parameter(description = "Resource ID") @PathVariable Long id,
            @Valid @RequestBody ResourceRequest request) {
        ResourceResponse updatedResource = resourceService.updateResource(id, request);
        return ResponseEntity.ok(
                ResponseData.success(HttpStatus.OK.value(), true, "Resource updated successfully", updatedResource)
        );
    }

    @PatchMapping("/{id}")
    @Operation(summary = "Delete a resource")
//    @PreAuthorize("hasAuthority('RESOURCE_DELETE')")
    public ResponseEntity<ResponseData<String>> deleteResource(
            @Parameter(description = "Resource ID") @PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.ok(
                ResponseData.success(HttpStatus.OK.value(), true, "Resource deleted successfully", null)
        );
    }


}
