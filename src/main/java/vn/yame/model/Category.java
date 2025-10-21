package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;

@Entity
@Table(name = "categories")
@Getter
@Setter
public class Category extends BaseEntity {
    private String name;
    @Column(unique = true, nullable = false)
    private String slug;
    private String description;

    @Enumerated(EnumType.STRING)
    private CommonStatus status = CommonStatus.ACTIVE;
}
