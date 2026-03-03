package vn.yame.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OrderStatusRequest {

    @NotBlank(message = "Order status name is required")
    @Size(min = 2, max = 100, message = "Name must be between 2 and 100 characters")
    private String name;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;

    @NotBlank(message = "Order status code is required")
    @Size(min = 2, max = 50, message = "Code must be between 2 and 50 characters")
    @Pattern(regexp = "^[A-Z0-9_]+$", message = "Code must contain only uppercase letters, digits and underscores")
    private String code;

    private boolean isFinal = false;
}

