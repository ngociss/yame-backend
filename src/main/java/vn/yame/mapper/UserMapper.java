package vn.yame.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.yame.dto.reponse.UserResponse;
import vn.yame.dto.request.UserCreateRequest;
import vn.yame.dto.request.UserUpdateRequest;
import vn.yame.model.Address;
import vn.yame.model.User;

@Mapper(componentModel = "spring")
public interface UserMapper {


    User toEntity(UserCreateRequest req);

    @Mapping(source = "status", target = "status")
    UserResponse toResponse(User user);

    @Mapping(target = "id", ignore = true)
        // tránh override id
    void updateUserFromDto(UserUpdateRequest dto, @MappingTarget User entity);
}
