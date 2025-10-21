package vn.yame.model;


import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.PurchaseOrderStatus;

import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "purchase_orders")
@Getter
@Setter
public class PurchaseOrder extends BaseEntity {
    private LocalDate orderDate;
    private PurchaseOrderStatus status;
    private BigDecimal totalAmount;

    @ManyToOne
    @JoinColumn(name = "supplier_id")
    private Supplier supplier;
}
