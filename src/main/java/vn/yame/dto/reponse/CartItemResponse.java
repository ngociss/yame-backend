package vn.yame.dto.reponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartItemResponse {

    private Long id;
    private int quantity;

    // ProductVariant info
    private Long productVariantId;
    private String skuCode;
    private int stockQuantity;

    // Product info
    private Long productId;
    private String productName;
    private String productSlug;
    private BigDecimal productPrice;
    private BigDecimal productDiscountPrice;

    // Color & Size info
    private String colorName;
    private String colorHexCode;
    private String sizeName;

    // Primary image
    private String primaryImageUrl;

    // Calculated fields
    private BigDecimal itemPrice;      // Giá 1 sản phẩm (sau discount nếu có)
    private BigDecimal totalPrice;     // itemPrice * quantity

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}

