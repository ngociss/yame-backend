package vn.yame.dto.request;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShippingMethodRequest {

    @NotBlank(message = "Corporation name is required")
    @Size(min = 2, max = 100, message = "Corporation name must be between 2 and 100 characters")
    private String corpName;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotNull(message = "Shipping cost is required")
    @DecimalMin(value = "0.0", inclusive = true, message = "Shipping cost must be greater than or equal to 0")
    private BigDecimal cost;

    @NotNull(message = "Estimated days is required")
    @Min(value = 1, message = "Estimated days must be at least 1")
    private Integer estimatedDays;
}

