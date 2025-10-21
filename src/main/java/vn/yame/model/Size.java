package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "sizes")
@Getter
@Setter
public class Size extends BaseEntity {
    private String name;
    private String description;
}
