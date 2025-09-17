package vn.yame.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.yame.dto.reponse.RoleResponse;
import vn.yame.dto.request.RoleRequest;
import vn.yame.model.Role;

@Mapper(componentModel = "spring")

public interface RoleMapper {

    Role toEntity(RoleRequest roleRequest);
    
    RoleResponse toResponse(Role entity);

    @Mapping(target = "id", ignore = true)
    public void updateRoleFromDto(RoleRequest dto, @MappingTarget Role entity);

}
