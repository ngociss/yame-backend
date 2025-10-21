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
public class CategoryRequest {

    @NotBlank(message = "Category name is required")
    @Size(min = 2, max = 100, message = "Category name must be between 2 and 100 characters")
    private String name;

    @NotBlank(message = "Category slug is required")
    @Pattern(regexp = "^[a-z0-9]+(?:-[a-z0-9]+)*$",
             message = "Slug must be lowercase, alphanumeric with hyphens only")
    @Size(min = 2, max = 100, message = "Slug must be between 2 and 100 characters")
    private String slug;

    @Size(max = 500, message = "Description cannot exceed 500 characters")
    private String description;
}
