package vn.yame.mapper;

import org.mapstruct.*;
import vn.yame.dto.request.ColorRequest;
import vn.yame.dto.reponse.ColorResponse;
import vn.yame.model.Color;

import java.util.List;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ColorMapper {

    ColorResponse toResponse(Color color);

    List<ColorResponse> toResponseList(List<Color> colors);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    Color toEntity(ColorRequest request);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "id", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "status", ignore = true)
    void updateEntity(@MappingTarget Color color, ColorRequest request);
}

