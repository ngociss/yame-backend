package vn.yame.mapper;

import org.mapstruct.*;
import vn.yame.dto.request.ProductVariantRequest;
import vn.yame.dto.reponse.ProductVariantResponse;
import vn.yame.model.ProductVariant;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductVariantMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    @Mapping(source = "color.id", target = "colorId")
    @Mapping(source = "color.name", target = "colorName")
    @Mapping(source = "color.hexCode", target = "colorHexCode")
    @Mapping(source = "size.id", target = "sizeId")
    @Mapping(source = "size.name", target = "sizeName")
    ProductVariantResponse toResponse(ProductVariant productVariant);

    List<ProductVariantResponse> toResponseList(List<ProductVariant> productVariants);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "color", ignore = true)
    @Mapping(target = "size", ignore = true)
    ProductVariant toEntity(ProductVariantRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "color", ignore = true)
    @Mapping(target = "size", ignore = true)
    void updateEntity(@MappingTarget ProductVariant productVariant, ProductVariantRequest request);
}

