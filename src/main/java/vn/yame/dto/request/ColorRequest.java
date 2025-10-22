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
public class ColorRequest {

    @NotBlank(message = "Color name is required")
    @Size(min = 2, max = 50, message = "Color name must be between 2 and 50 characters")
    private String name;

    @NotBlank(message = "Hex code is required")
    @Pattern(regexp = "^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
             message = "Hex code must be valid format (e.g., #FFFFFF or #FFF)")
    private String hexCode;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}

