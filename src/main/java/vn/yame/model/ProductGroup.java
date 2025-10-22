package vn.yame.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_groups")
@Getter
@Setter

public class ProductGroup extends BaseEntity {
    private String name;
    private String description;
}
