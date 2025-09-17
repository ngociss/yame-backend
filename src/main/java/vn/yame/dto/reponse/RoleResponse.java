package vn.yame.dto.reponse;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private List<Long> userIds;
}
