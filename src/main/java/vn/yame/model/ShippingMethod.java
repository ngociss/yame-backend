package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;

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

    @Enumerated(EnumType.STRING)
    private CommonStatus status = CommonStatus.ACTIVE;

    @OneToMany
    @JoinColumn(name = "shipping_method_id")
    private List<Order> orders;
}
