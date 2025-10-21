package vn.yame.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.DiscountStatus;
import vn.yame.common.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Getter
@Setter
public class Discount extends BaseEntity {
    private String code;
    private String description;
    @Enumerated(EnumType.STRING)
    private DiscountType type;
    @Enumerated(EnumType.STRING)
    private DiscountStatus discountStatus;
    @Enumerated(EnumType.STRING)
    private CommonStatus status = CommonStatus.ACTIVE;
    @Column(precision = 10, scale = 2)
    private BigDecimal discountValue;

    private Double minOrderAmount;
    private Double maxDiscountAmount;
    private Integer usageLimit;
    private LocalDateTime startAt;
    private LocalDateTime endAt;

    @OneToMany
    @JoinColumn(name = "discount_id")
    private java.util.List<Order> orders;
}
