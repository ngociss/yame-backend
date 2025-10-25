package vn.yame.dto.request;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariantRequest {

    @NotNull(message = "Product ID is required")
    private Long productId;

    @NotNull(message = "Color ID is required")
    private Long colorId;

    @NotNull(message = "Size ID is required")
    private Long sizeId;

    @NotBlank(message = "SKU code is required")
    @Size(min = 3, max = 50, message = "SKU code must be between 3 and 50 characters")
    private String skuCode;

    @NotNull(message = "Stock quantity is required")
    @Min(value = 0, message = "Stock quantity must be greater than or equal to 0")
    private Integer stockQuantity;
}

