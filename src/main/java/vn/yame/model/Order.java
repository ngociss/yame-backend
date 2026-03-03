package vn.yame.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "orders")
@Getter
@Setter
public class Order extends BaseEntity {
    private int discountAmount;
    private BigDecimal totalCost;
    private boolean isFreeShip;
    private LocalDateTime shippedAt;
    private LocalDateTime deliveredAt;
    private BigDecimal shippingCost;

    @ManyToOne
    @JoinColumn(name = "orderStatus_id")
    private OrderStatus orderStatus;

    @ManyToOne
    @JoinColumn(name = "shipping_method_id")
    private ShippingMethod shippingMethod;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL)
    private List<OrderItem> orderItems;

    @ManyToOne
    @JoinColumn(name="discount_id" )
    private Discount discount;

    @ManyToOne
    @JoinColumn(name="user_id" )
    private User user;

    @OneToOne(mappedBy = "order", cascade = CascadeType.ALL)
    private Payment payment;
}
