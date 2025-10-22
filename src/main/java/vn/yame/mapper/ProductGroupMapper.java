package vn.yame.mapper;

import org.mapstruct.*;
import vn.yame.dto.request.ProductGroupRequest;
import vn.yame.dto.reponse.ProductGroupResponse;
import vn.yame.model.ProductGroup;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductGroupMapper {

    ProductGroupResponse toResponse(ProductGroup productGroup);

    List<ProductGroupResponse> toResponseList(List<ProductGroup> productGroups);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    ProductGroup toEntity(ProductGroupRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget ProductGroup productGroup, ProductGroupRequest request);
}

