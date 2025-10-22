package vn.yame.mapper;

import org.mapstruct.*;
import vn.yame.dto.request.ProductRequest;
import vn.yame.dto.reponse.ProductResponse;
import vn.yame.model.Product;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ProductMapper {

    @Mapping(source = "category.id", target = "categoryId")
    @Mapping(source = "category.name", target = "categoryName")
    @Mapping(source = "category.slug", target = "categorySlug")
    @Mapping(source = "material.id", target = "materialId")
    @Mapping(source = "material.name", target = "materialName")
    ProductResponse toResponse(Product product);

    List<ProductResponse> toResponseList(List<Product> products);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "productStatus", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "material", ignore = true)
    Product toEntity(ProductRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "productStatus", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "material", ignore = true)
    void updateEntity(@MappingTarget Product product, ProductRequest request);
}

