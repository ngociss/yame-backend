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
public class OrderStatus {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String description;
    private String code;
    private boolean isFinal;

    @OneToMany(mappedBy = "orderStatus")
    private List<Order> orders;
}
