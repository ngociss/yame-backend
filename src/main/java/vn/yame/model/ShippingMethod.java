package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Entity
@Table(name = "shipping_methods")
@Getter
@Setter
public class ShippingMethod extends BaseEntity {
    private String corpName;
    private String description;
    private BigDecimal cost;
    private int estimatedDays;
    private int status;
    private boolean isVerified;

    @OneToMany
    @JoinColumn(name = "shipping_method_id")
    private List<Order> orders;
}
