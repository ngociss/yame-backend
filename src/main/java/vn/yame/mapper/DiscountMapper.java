
package vn.yame.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import vn.yame.common.enums.DiscountStatus;
import vn.yame.dto.reponse.DiscountResponse;
import vn.yame.dto.request.DiscountRequest;
import vn.yame.model.Discount;

import java.time.LocalDateTime;

@Mapper(componentModel = "spring")
public interface DiscountMapper {

    @Mapping(target = "discountStatus", source = ".", qualifiedByName = "determineDiscountStatus")
    @Mapping(target = "usedCount", source = "orders", qualifiedByName = "countOrders")
    DiscountResponse toResponse(Discount discount);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "discountStatus", constant = "ACTIVE")
    Discount toEntity(DiscountRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "discountStatus", ignore = true)
    void updateEntity(DiscountRequest request, @MappingTarget Discount discount);

    @Named("determineDiscountStatus")
    default DiscountStatus determineDiscountStatus(Discount discount) {
        LocalDateTime now = LocalDateTime.now();

        if (discount.getEndAt() != null && discount.getEndAt().isBefore(now)) {
            return DiscountStatus.EXPIRED;
        }

        if (discount.getUsageLimit() != null && discount.getOrders() != null
                && discount.getOrders().size() >= discount.getUsageLimit()) {
            return DiscountStatus.USED_UP;
        }

        if (discount.getStartAt() != null && discount.getStartAt().isAfter(now)) {
            return DiscountStatus.INACTIVE;
        }

        return DiscountStatus.ACTIVE;
    }

    @Named("countOrders")
    default Integer countOrders(java.util.List<?> orders) {
        return orders != null ? orders.size() : 0;
    }
}
