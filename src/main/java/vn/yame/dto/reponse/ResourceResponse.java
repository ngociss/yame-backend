package vn.yame.dto.reponse;

import lombok.Data;
import vn.yame.common.enums.CommonStatus;

import java.time.LocalDateTime;
import java.util.List;

@Data
public class ResourceResponse {
    private Long id;
    private String name;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private CommonStatus status;

    // List of permission names under this resource
    private List<String> permissionNames;

    // Count of permissions under this resource
    private Integer permissionCount;
}
