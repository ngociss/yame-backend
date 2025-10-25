package vn.yame.dto.reponse;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ProductStatus;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductResponse {

    private Long id;
    private String name;
    private String slug;
    private BigDecimal basePrice;
    private BigDecimal discountPrice;
    private CommonStatus status;
    private ProductStatus productStatus;
    private String imageUrl;

    // Category info
    private Long categoryId;
    private String categoryName;
    private String categorySlug;

    // Material info
    private Long materialId;
    private String materialName;

    // Product group info
    private Long productGroupId;
    private String productGroupName;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}
