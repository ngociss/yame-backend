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
import java.util.stream.Collectors;

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

        // If this is the first address or explicitly set as default, make it default
        if (addressRequest.isDefault() || !addressRepository.existsAddressByUser(user)) {
            // Remove default from existing addresses
            setOtherAddressesAsNonDefault(user);
            address.setDefaultAddress(true);
        } else {
            address.setDefaultAddress(false);
        }

        Address savedAddress = addressRepository.save(address);
        log.info("Address created successfully with id: {}", savedAddress.getId());

        return addressMapper.toResponse(savedAddress);
    }

    @Override
    public AddressResponse updateAddress(Long id, AddressRequest addressRequest) {
        log.info("Updating address with id: {}", id);

        Address address = addressRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.ADDRESS_NOT_FOUND,
                "Address not found with id: " + id
            ));

        // Verify user ownership
        if (!address.getUser().getId().equals(addressRequest.getUserId())) {
            throw new InvalidDataException(
                ErrorCode.UNAUTHORIZED,
                "You are not authorized to update this address"
            );
        }

        // Update fields
        addressMapper.updateAddress(addressRequest, address);

        // Handle default flag
        if (addressRequest.isDefault() && !address.isDefaultAddress()) {
            setOtherAddressesAsNonDefault(address.getUser());
            address.setDefaultAddress(true);
        }

        Address updatedAddress = addressRepository.save(address);
        log.info("Address updated successfully with id: {}", updatedAddress.getId());

        return addressMapper.toResponse(updatedAddress);
    }

    @Override
    public void deleteAddress(Long addressId) {
        log.info("Deleting address with id: {}", addressId);

        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.ADDRESS_NOT_FOUND,
                "Address not found with id: " + addressId
            ));

        // Check if this is the default address
        if (address.isDefaultAddress()) {
            throw new InvalidDataException(
                ErrorCode.DEFAULT_ADDRESS_REQUIRED,
                "Cannot delete default address. Please set another address as default first."
            );
        }

        addressRepository.delete(address);
        log.info("Address deleted successfully with id: {}", addressId);
    }

    @Override
    @Transactional(readOnly = true)
    public List<AddressResponse> getAddressesByUser(Long userId) {
        log.info("Fetching all addresses for user: {}", userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.USER_NOT_FOUND,
                "User not found with id: " + userId
            ));

        List<Address> addresses = addressRepository.findByUser(user);
        return addresses.stream()
                .map(addressMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public AddressResponse getAddressById(Long addressId) {
        log.info("Fetching address with id: {}", addressId);

        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.ADDRESS_NOT_FOUND,
                "Address not found with id: " + addressId
            ));

        return addressMapper.toResponse(address);
    }

    @Override
    public AddressResponse setDefaultAddress(Long addressId, Long userId) {
        log.info("Setting default address: {} for user: {}", addressId, userId);

        User user = userRepository.findById(userId)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.USER_NOT_FOUND,
                "User not found with id: " + userId
            ));

        Address address = addressRepository.findById(addressId)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.ADDRESS_NOT_FOUND,
                "Address not found with id: " + addressId
            ));

        // Verify user ownership
        if (!address.getUser().getId().equals(userId)) {
            throw new InvalidDataException(
                ErrorCode.UNAUTHORIZED,
                "You are not authorized to modify this address"
            );
        }

        // Remove default from other addresses
        setOtherAddressesAsNonDefault(user);

        // Set as default
        address.setDefaultAddress(true);
        Address savedAddress = addressRepository.save(address);

        log.info("Default address set successfully");
        return addressMapper.toResponse(savedAddress);
    }

    private void setOtherAddressesAsNonDefault(User user) {
        Address existingDefaultAddress = addressRepository.findByUserAndDefaultAddress(user, true)
                .orElse(null);
        if (existingDefaultAddress != null) {
            existingDefaultAddress.setDefaultAddress(false);
            addressRepository.save(existingDefaultAddress);
        }
    }
}
