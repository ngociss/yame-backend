package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "materials")
@Getter
@Setter
public class Material extends BaseEntity {
    private String name;
    private String description;
    private boolean isActive;
}
