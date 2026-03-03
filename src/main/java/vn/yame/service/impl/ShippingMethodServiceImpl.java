package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.request.ShippingMethodRequest;
import vn.yame.dto.reponse.ShippingMethodResponse;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.ShippingMethodMapper;
import vn.yame.model.ShippingMethod;
import vn.yame.repository.ShippingMethodRepository;
import vn.yame.service.ShippingMethodService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ShippingMethodServiceImpl implements ShippingMethodService {

    private final ShippingMethodRepository shippingMethodRepository;
    private final ShippingMethodMapper shippingMethodMapper;

    @Override
    public ShippingMethodResponse createShippingMethod(ShippingMethodRequest request) {
        log.info("Creating new shipping method with corpName: {}", request.getCorpName());

        if (shippingMethodRepository.existsByCorpName(request.getCorpName())) {
            throw new ExistingResourcesException(
                ErrorCode.SHIPPING_METHOD_NOT_FOUND,
                "Shipping method with name '" + request.getCorpName() + "' already exists"
            );
        }

        ShippingMethod shippingMethod = shippingMethodMapper.toEntity(request);
        shippingMethod.setStatus(CommonStatus.ACTIVE);
        ShippingMethod saved = shippingMethodRepository.save(shippingMethod);

        log.info("Shipping method created successfully with id: {}", saved.getId());
        return shippingMethodMapper.toResponse(saved);
    }

    @Override
    public ShippingMethodResponse updateShippingMethod(Long id, ShippingMethodRequest request) {
        log.info("Updating shipping method with id: {}", id);

        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SHIPPING_METHOD_NOT_FOUND,
                "Shipping method not found with id: " + id
            ));

        if (!shippingMethod.getCorpName().equals(request.getCorpName()) &&
            shippingMethodRepository.existsByCorpNameAndIdNot(request.getCorpName(), id)) {
            throw new ExistingResourcesException(
                ErrorCode.SHIPPING_METHOD_NOT_FOUND,
                "Shipping method with name '" + request.getCorpName() + "' already exists"
            );
        }

        shippingMethodMapper.updateEntity(shippingMethod, request);
        ShippingMethod updated = shippingMethodRepository.save(shippingMethod);

        log.info("Shipping method updated successfully with id: {}", id);
        return shippingMethodMapper.toResponse(updated);
    }

    @Override
    public void deleteShippingMethod(Long id) {
        log.info("Soft deleting shipping method with id: {}", id);

        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SHIPPING_METHOD_NOT_FOUND,
                "Shipping method not found with id: " + id
            ));

        shippingMethod.setStatus(CommonStatus.INACTIVE);
        shippingMethodRepository.save(shippingMethod);

        log.info("Shipping method soft deleted (INACTIVE) with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ShippingMethodResponse getShippingMethodById(Long id) {
        log.info("Fetching shipping method with id: {}", id);

        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SHIPPING_METHOD_NOT_FOUND,
                "Shipping method not found with id: " + id
            ));

        return shippingMethodMapper.toResponse(shippingMethod);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ShippingMethodResponse> getAllShippingMethods(Pageable pageable) {
        log.info("Fetching all shipping methods with pagination - page: {}, size: {}",
            pageable.getPageNumber(), pageable.getPageSize());

        return shippingMethodRepository.findAll(pageable)
            .map(shippingMethodMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ShippingMethodResponse> getActiveShippingMethods() {
        log.info("Fetching all active shipping methods");

        List<ShippingMethod> methods = shippingMethodRepository.findByStatus(CommonStatus.ACTIVE);
        return shippingMethodMapper.toResponseList(methods);
    }

    @Override
    public ShippingMethodResponse updateStatus(Long id, CommonStatus status) {
        log.info("Updating status for shipping method with id: {} to {}", id, status);

        ShippingMethod shippingMethod = shippingMethodRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SHIPPING_METHOD_NOT_FOUND,
                "Shipping method not found with id: " + id
            ));

        shippingMethod.setStatus(status);
        ShippingMethod updated = shippingMethodRepository.save(shippingMethod);

        log.info("Shipping method status updated successfully with id: {}", id);
        return shippingMethodMapper.toResponse(updated);
    }
}

