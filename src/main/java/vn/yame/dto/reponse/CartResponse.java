package vn.yame.dto.reponse;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartResponse {

    private Long id;
    private Long userId;
    private String userEmail;
    private boolean isVerified;

    private List<CartItemResponse> items = new ArrayList<>();

    // Summary fields
    private int totalItems;              // Tổng số items trong giỏ
    private int totalQuantity;           // Tổng số lượng sản phẩm
    private BigDecimal subtotal;         // Tổng tiền trước giảm giá
    private BigDecimal discountAmount;   // Tổng tiền giảm giá (nếu c��)
    private BigDecimal total;            // Tổng tiền sau giảm giá
}

