package vn.yame.service;

import vn.yame.dto.reponse.AddressResponse;
import vn.yame.dto.request.AddressRequest;

import java.util.List;

public interface AddressService {
    AddressResponse createAddress(AddressRequest addressRequest);

    AddressResponse updateAddress(Long id, AddressRequest addressRequest);

    void deleteAddress(Long addressId);

    List<AddressResponse> getAddressesByUser(Long userId);

    AddressResponse getAddressById(Long addressId);

    AddressResponse setDefaultAddress(Long addressId, Long userId);
}
