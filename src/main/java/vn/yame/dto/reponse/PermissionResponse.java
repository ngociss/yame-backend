package vn.yame.dto.reponse;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Set;

@Data
public class PermissionResponse {
    private Long id;
    private String name;
    private String description;
    private String code;
    private boolean isActive;
    private boolean isVerified;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Resource info
    private Long resourceId;
    private String resourceName;

    // Role names that have this permission
    private Set<String> roleNames;
}
