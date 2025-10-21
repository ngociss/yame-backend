package vn.yame.service.impl;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.reponse.AddressResponse;
import vn.yame.dto.request.AddressRequest;
import vn.yame.exception.InvalidDataException;
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
@Slf4j
@Transactional
public class AddressServiceImpl implements AddressService {
    private final AddressRepository addressRepository;
    private final AddressMapper addressMapper;
    private final UserRepository userRepository;

    @Override
    public AddressResponse createAddress(AddressRequest addressRequest) {
        log.info("Creating new address for user: {}", addressRequest.getUserId());

        User user = userRepository.findById(addressRequest.getUserId())
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.USER_NOT_FOUND,
                "User not found with id: " + addressRequest.getUserId()
            ));

        Address address = addressMapper.toAddress(addressRequest);
        address.setUser(user);

        // If this is the first address, set it as default
        if (addressRepository.existsAddressByUser(user)) {
            address.setDefault(false);
        } else {
            address.setDefault(true);
        }

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully with id: {}", savedAddress.getId());

        return addressMapper.toResponse(savedAddress);
    }

    @Override
    public AddressResponse updateAddress(AddressRequest addressRequest) {
        log.info("Updating address with id: {}", addressRequest.getUserId());

        // Implementation for update
        return null;
    }

    @Override
    public void deleteAddress(Long addressId) {
        log.info("Soft deleting address with id: {}", addressId);

        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.ADDRESS_NOT_FOUND,
                "Address not found with id: " + addressId
            ));

        // Check if this is the default address
        if (address.isDefault()) {
            throw new InvalidDataException(
                ErrorCode.DEFAULT_ADDRESS_REQUIRED,
                "Cannot delete default address. Please set another address as default first."
            );
        }

        // Soft delete - simply remove from database (addresses can be hard deleted)
        // Or you can add a 'deleted' flag if needed
        addressRepository.delete(address);

        log.info("Address deleted successfully with id: {}", addressId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddresses() {
        log.info("Fetching all addresses");
        return List.of();
    }

    @Override
    public void setDefaultAddress(Long addressId, Long userId) {
        log.info("Setting default address: {} for user: {}", addressId, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.USER_NOT_FOUND,
                "User not found with id: " + userId
            ));

        // Remove default from existing address
        Address existingDefaultAddress = addressRepository.findAddressByUserAndIsDefault(user, true)
                .orElse(null);
        if (existingDefaultAddress != null) {
            existingDefaultAddress.setDefault(false);
            addressRepository.save(existingDefaultAddress);
        }

        // Set new default address
        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.ADDRESS_NOT_FOUND,
                "Address not found with id: " + addressId
            ));

        address.setDefault(true);
        addressRepository.save(address);

        log.info("Default address set successfully");
    }
}
