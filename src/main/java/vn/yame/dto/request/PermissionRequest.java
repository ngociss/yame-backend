package vn.yame.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class PermissionRequest {

    @NotBlank(message = "Permission name is required")
    @Size(min = 2, max = 100, message = "Permission name must be between 2 and 100 characters")
    private String name;

    @Size(max = 255, message = "Description cannot exceed 255 characters")
    private String description;

    @NotBlank(message = "Permission code is required")
    @Size(min = 2, max = 50, message = "Permission code must be between 2 and 50 characters")
    private String code;

    @NotNull(message = "Resource ID is required")
    private Long resourceId;

    private boolean isActive = true;
}
