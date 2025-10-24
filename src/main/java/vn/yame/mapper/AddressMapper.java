package vn.yame.mapper;

import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import vn.yame.dto.reponse.AddressResponse;
import vn.yame.dto.request.AddressRequest;
import vn.yame.model.Address;

@Mapper(componentModel = "spring")
public interface AddressMapper {

    Address toAddress(AddressRequest req);

    AddressResponse toResponse(Address address);

    @AfterMapping
    default void mapFullAddress(Address address, @MappingTarget AddressResponse response) {
        String fullAddress = String.join(", ",
                address.getStreetAddress(),
                address.getWard(),
                address.getProvince()
        );
        response.setAddress(fullAddress);
    }

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "user", ignore = true)
    void updateAddress(AddressRequest dto, @MappingTarget Address entity);
}
