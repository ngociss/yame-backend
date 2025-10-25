package vn.yame.dto.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductImageRequest {

    @NotBlank(message = "Image URL is required")
    private String imageUrl;

    @Size(max = 255, message = "Alt text cannot exceed 255 characters")
    private String altText;

    private boolean isPrimary = false;

    private int displayOrder = 0;
}

