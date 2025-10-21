package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "colors")
@Getter
@Setter
public class Color extends BaseEntity {
    private String name;
    private String hexCode;
    private String description;
    private boolean isActive;
}
