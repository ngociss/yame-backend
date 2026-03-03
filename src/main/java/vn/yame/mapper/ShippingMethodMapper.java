package vn.yame.mapper;

import org.mapstruct.*;
import vn.yame.dto.request.ShippingMethodRequest;
import vn.yame.dto.reponse.ShippingMethodResponse;
import vn.yame.model.ShippingMethod;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ShippingMethodMapper {

    ShippingMethodResponse toResponse(ShippingMethod shippingMethod);

    List<ShippingMethodResponse> toResponseList(List<ShippingMethod> shippingMethods);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "orders", ignore = true)
    ShippingMethod toEntity(ShippingMethodRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "orders", ignore = true)
    void updateEntity(@MappingTarget ShippingMethod shippingMethod, ShippingMethodRequest request);
}

