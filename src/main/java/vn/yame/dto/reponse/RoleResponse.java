package vn.yame.dto.reponse;

import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private CommonStatus status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Permission information
    private Set<String> permissionNames;
    private Integer permissionCount;

    // User information
    private List<Long> userIds;
    private Integer userCount;
}
