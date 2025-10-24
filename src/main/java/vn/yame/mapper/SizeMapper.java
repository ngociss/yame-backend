package vn.yame.mapper;

import org.mapstruct.*;
import vn.yame.dto.request.SizeRequest;
import vn.yame.dto.reponse.SizeResponse;
import vn.yame.model.Size;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface SizeMapper {

    SizeResponse toResponse(Size size);

    List<SizeResponse> toResponseList(List<Size> sizes);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Size toEntity(SizeRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget Size size, SizeRequest request);
}

