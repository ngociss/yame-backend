package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.ProductStatus;

import java.math.BigDecimal;

@Entity
@Table(name = "products")
@Getter
@Setter
public class Product extends BaseEntity {
    private String name;
    @Column(unique = true, nullable = false)
    private String slug;
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
    private boolean isActive;
    private ProductStatus status;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

}
