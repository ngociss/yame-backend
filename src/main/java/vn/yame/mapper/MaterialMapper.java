package vn.yame.mapper;

import org.mapstruct.*;
import vn.yame.dto.request.MaterialRequest;
import vn.yame.dto.reponse.MaterialResponse;
import vn.yame.model.Material;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface MaterialMapper {

    MaterialResponse toResponse(Material material);

    List<MaterialResponse> toResponseList(List<Material> materials);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Material toEntity(MaterialRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget Material material, MaterialRequest request);
}

