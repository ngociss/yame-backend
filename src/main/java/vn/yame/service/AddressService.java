package vn.yame.service;

import vn.yame.dto.reponse.AddressResponse;
import vn.yame.dto.request.AddressRequest;

import java.util.List;

public interface AddressService {
    public AddressResponse createAddress(AddressRequest addressRequest);

    public AddressResponse updateAddress(AddressRequest addressRequest);

    public void deleteAddress(Long addressId);

    public List<AddressResponse> getAddresses();

    public void setDefaultAddress(Long addressId, Long userId);
}
