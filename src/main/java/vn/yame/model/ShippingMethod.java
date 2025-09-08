package vn.yame.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "shipping_methods")
@Getter
@Setter
public class ShippingMethod {
    @Id
    @GeneratedValue(strategy = jakarta.persistence.GenerationType.IDENTITY)
    private Long id;
    private String corpName;
    private String description;
    private BigDecimal cost;
    private int estimatedDays;
    private int status;

    @CreationTimestamp
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;
    private boolean isVerified;

    @OneToMany
    @JoinColumn(name = "shipping_method_id")
    private List<Order> orders;


}
