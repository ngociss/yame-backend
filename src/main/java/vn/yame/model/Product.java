package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;
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

    @Enumerated(EnumType.STRING)
    private CommonStatus status = CommonStatus.ACTIVE;

    @Enumerated(EnumType.STRING)
    private ProductStatus productStatus;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToOne
    @JoinColumn(name = "material_id")
    private Material material;

    @ManyToOne
    @JoinColumn(name = "product_group_id")
    private ProductGroup productGroup;

    @ManyToOne
    @JoinColumn(name = "color_id")
    private Color color;
}
