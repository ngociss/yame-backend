package vn.yame.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import vn.yame.dto.reponse.ResourceResponse;
import vn.yame.dto.request.ResourceRequest;
import vn.yame.model.Permission;
import vn.yame.model.Resource;

import java.util.List;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring")
public interface ResourceMapper {

    @Mapping(source = "permissions", target = "permissionNames", qualifiedByName = "permissionsToNames")
    @Mapping(source = "permissions", target = "permissionCount", qualifiedByName = "permissionsToCount")
    @Mapping(source = "status", target = "status")
    ResourceResponse toResponse(Resource resource);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Resource toEntity(ResourceRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntityFromRequest(@MappingTarget Resource resource, ResourceRequest request);

    @Named("permissionsToNames")
    default List<String> permissionsToNames(List<Permission> permissions) {
        if (permissions == null) {
            return null;
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toList());
    }

    @Named("permissionsToCount")
    default int permissionsToCount(List<Permission> permissions) {
        return permissions != null ? permissions.size() : 0;
    }
}
