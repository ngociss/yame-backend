package vn.yame.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.yame.dto.reponse.AddressResponse;
import vn.yame.dto.request.AddressRequest;
import vn.yame.dto.request.UserCreateRequest;
import vn.yame.model.Address;
import vn.yame.model.User;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toAddress(AddressRequest req);

    AddressResponse toResponse(Address address);

    @AfterMapping
    default void mapFullAddress(Address address, @MappingTarget AddressResponse response) {
        String fullAddress = String.join(", ",
                address.getStreetAddress(),
                address.getDistrict(),
                address.getCity()
        );
        response.setAddress(fullAddress);
    }

    @Mapping(target = "id", ignore = true)
        // tránh override id
    void updateAddress(AddressRequest dto, @MappingTarget Address entity);
}
