package vn.yame.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;

import java.util.List;

@Entity
@Table(name = "resources",
       uniqueConstraints = {
           @UniqueConstraint(name = "uk_resource_name", columnNames = "name")
       })
@Getter
@Setter
public class Resource extends BaseEntity {
    @Column(nullable = false, unique = true, length = 100)
    private String name;

    @Column(length = 255)
    private String description;

    @Enumerated(EnumType.STRING)
    private CommonStatus status;

    @OneToMany(mappedBy = "resource")
    private List<Permission> permissions;
}
