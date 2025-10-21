package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;

@Entity
@Table(name = "sizes")
@Getter
@Setter
public class Size extends BaseEntity {
    private String name;
    private String description;

    @Enumerated(EnumType.STRING)
    private CommonStatus status = CommonStatus.ACTIVE;
}
