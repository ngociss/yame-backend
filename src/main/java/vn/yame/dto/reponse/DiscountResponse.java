package vn.yame.dto.reponse;

import lombok.Getter;
import lombok.Setter;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.DiscountStatus;
import vn.yame.common.enums.DiscountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
public class DiscountResponse {
    private Long id;
    private String code;
    private String description;
    private DiscountType type;
    private DiscountStatus discountStatus;
    private CommonStatus status;
    private BigDecimal discountValue;
    private Double minOrderAmount;
    private Double maxDiscountAmount;
    private Integer usageLimit;
    private Integer usedCount;
    private LocalDateTime startAt;
    private LocalDateTime endAt;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

