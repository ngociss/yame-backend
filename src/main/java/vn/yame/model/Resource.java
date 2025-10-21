package vn.yame.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;

import java.util.List;

@Entity
@Table(name = "resources")
@Getter
@Setter
public class Resource extends BaseEntity {
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private CommonStatus status;

    @OneToMany(mappedBy = "resource")
    private List<Permission> permissions;
}
