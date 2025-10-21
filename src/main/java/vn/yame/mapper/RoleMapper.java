package vn.yame.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.Named;
import org.mapstruct.ReportingPolicy;
import vn.yame.dto.reponse.RoleResponse;
import vn.yame.dto.request.RoleRequest;
import vn.yame.model.Permission;
import vn.yame.model.Role;
import vn.yame.model.User;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Role toEntity(RoleRequest roleRequest);
    
    @Mapping(source = "permissions", target = "permissionNames", qualifiedByName = "permissionsToNames")
    @Mapping(source = "permissions", target = "permissionCount", qualifiedByName = "permissionsToCount")
    @Mapping(source = "users", target = "userIds", qualifiedByName = "usersToIds")
    @Mapping(source = "users", target = "userCount", qualifiedByName = "usersToCount")
    RoleResponse toResponse(Role entity);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "permissions", ignore = true)
    @Mapping(target = "users", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(@MappingTarget Role entity, RoleRequest dto);

    @Named("permissionsToNames")
    default Set<String> permissionsToNames(Set<Permission> permissions) {
        if (permissions == null || permissions.isEmpty()) {
            return null;
        }
        return permissions.stream()
                .map(Permission::getName)
                .collect(Collectors.toSet());
    }

    @Named("permissionsToCount")
    default Integer permissionsToCount(Set<Permission> permissions) {
        if (permissions == null) {
            return 0;
        }
        return permissions.size();
    }

    @Named("usersToIds")
    default List<Long> usersToIds(Set<User> users) {
        if (users == null || users.isEmpty()) {
            return null;
        }
        return users.stream()
                .map(User::getId)
                .collect(Collectors.toList());
    }

    @Named("usersToCount")
    default Integer usersToCount(Set<User> users) {
        if (users == null) {
            return 0;
        }
        return users.size();
    }
}
