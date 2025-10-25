package vn.yame.mapper;

import org.mapstruct.*;
import vn.yame.dto.request.ProductImageRequest;
import vn.yame.dto.reponse.ProductImageResponse;
import vn.yame.model.ProductImage;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductImageMapper {

    @Mapping(source = "product.id", target = "productId")
    @Mapping(source = "product.name", target = "productName")
    ProductImageResponse toResponse(ProductImage productImage);

    List<ProductImageResponse> toResponseList(List<ProductImage> productImages);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "product", ignore = true)
    ProductImage toEntity(ProductImageRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "product", ignore = true)
    void updateEntity(@MappingTarget ProductImage productImage, ProductImageRequest request);
}

