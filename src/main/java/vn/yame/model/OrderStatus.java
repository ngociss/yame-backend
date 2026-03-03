package vn.yame.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@SuppressWarnings("ALL")
@Entity
@Table(name = "order_status")
@Getter
@Setter
public class OrderStatus extends BaseEntity {
    private String name;
    private String description;
    private String code;

    @Column(name = "is_final", nullable = false)
    private boolean isFinal;

    @OneToMany(mappedBy = "orderStatus")
    private List<Order> orders;
}
