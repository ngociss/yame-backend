package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "product_images")
@Getter
@Setter
public class ProductImage extends BaseEntity {
    private String imageUrl;
    private String altText;
    private boolean isPrimary;
    private int displayOrder;    // thứ tự hiển thị

    @ManyToOne
    @JoinColumn(name = "product_id")
    private Product product;
}
