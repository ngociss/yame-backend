package vn.yame.service.impl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import vn.yame.dto.reponse.AddressResponse;
import vn.yame.dto.request.AddressRequest;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.AddressMapper;
import vn.yame.model.Address;
import vn.yame.model.User;
import vn.yame.repository.AddressRepository;
import vn.yame.repository.UserRepository;
import vn.yame.service.AddressService;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    @Override
    public AddressResponse createAddress(AddressRequest addressRequest) {
        User user = userRepository.findById(addressRequest.getUserId()).orElseThrow(() -> new NotFoundResourcesException("User not found"));
        Address address = addressMapper.toAddress(addressRequest);
        address.setUser(user);
        if (addressRepository.existsAddressByUser(user)) {
            address.setDefault(false);
        } else {
            address.setDefault(true);
        }
        Address savedAddress = addressRepository.save(address);
        return addressMapper.toResponse(savedAddress);
    }

    @Override
    public AddressResponse updateAddress(AddressRequest addressRequest) {
        return null;
    }

    @Override
    public void deleteAddress(Long addressId) {

    }

    @Override
    public List<AddressResponse> getAddresses() {
        return List.of();
    }

    @Override
    public void setDefaultAddress(Long addressId, Long userId) {
        User user = userRepository.findById(userId).orElseThrow(() -> new NotFoundResourcesException("User not found"));
        Address existingDefaultAddress = addressRepository.findAddressByUserAndIsDefault(user, true)
                .orElse(null);
        if (existingDefaultAddress != null) {
            existingDefaultAddress.setDefault(false);
            addressRepository.save(existingDefaultAddress);
        }
        Address address = addressRepository.findById(addressId).orElseThrow(() -> new NotFoundResourcesException("Address not found"));
        address.setDefault(true);
        addressRepository.save(address);
    }
}
