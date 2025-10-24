package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.reponse.DiscountResponse;
import vn.yame.dto.request.DiscountRequest;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.InvalidDataException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.DiscountMapper;
import vn.yame.model.Discount;
import vn.yame.repository.DiscountRepository;
import vn.yame.service.DiscountService;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class DiscountServiceImpl implements DiscountService {

    private final DiscountRepository discountRepository;
    private final DiscountMapper discountMapper;

    @Override
    public DiscountResponse createDiscount(DiscountRequest request) {
        log.info("Creating new discount with code: {}", request.getCode());

        // Validate discount code uniqueness
        if (discountRepository.existsByCode(request.getCode())) {
            throw new ExistingResourcesException(
                ErrorCode.DISCOUNT_NOT_FOUND,
                "Discount code already exists: " + request.getCode()
            );
        }

        // Validate date range
        validateDateRange(request.getStartAt(), request.getEndAt());

        // Validate discount value
        validateDiscountValue(request);

        Discount discount = discountMapper.toEntity(request);
        discount.setStatus(CommonStatus.ACTIVE);

        Discount savedDiscount = discountRepository.save(discount);
        log.info("Discount created successfully with id: {}", savedDiscount.getId());

        return discountMapper.toResponse(savedDiscount);
    }

    @Override
    public DiscountResponse updateDiscount(Long id, DiscountRequest request) {
        log.info("Updating discount with id: {}", id);

        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.DISCOUNT_NOT_FOUND,
                "Discount not found with id: " + id
            ));

        // Validate code uniqueness if changed
        if (!discount.getCode().equals(request.getCode())) {
            if (discountRepository.existsByCodeAndIdNot(request.getCode(), id)) {
                throw new ExistingResourcesException(
                    ErrorCode.DISCOUNT_NOT_FOUND,
                    "Discount code already exists: " + request.getCode()
                );
            }
        }

        // Validate date range
        validateDateRange(request.getStartAt(), request.getEndAt());

        // Validate discount value
        validateDiscountValue(request);

        discountMapper.updateEntity(request, discount);

        Discount updatedDiscount = discountRepository.save(discount);
        log.info("Discount updated successfully with id: {}", updatedDiscount.getId());

        return discountMapper.toResponse(updatedDiscount);
    }

    @Override
    public void deleteDiscount(Long id) {
        log.info("Deleting discount with id: {}", id);

        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.DISCOUNT_NOT_FOUND,
                "Discount not found with id: " + id
            ));

        // Check if discount has been used
        if (discount.getOrders() != null && !discount.getOrders().isEmpty()) {
            throw new InvalidDataException(
                ErrorCode.DISCOUNT_ALREADY_APPLIED,
                "Cannot delete discount that has been used in orders"
            );
        }

        discountRepository.delete(discount);
        log.info("Discount deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountById(Long id) {
        log.info("Fetching discount with id: {}", id);

        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.DISCOUNT_NOT_FOUND,
                "Discount not found with id: " + id
            ));

        return discountMapper.toResponse(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse getDiscountByCode(String code) {
        log.info("Fetching discount with code: {}", code);

        Discount discount = discountRepository.findByCode(code)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.DISCOUNT_NOT_FOUND,
                "Discount not found with code: " + code
            ));

        return discountMapper.toResponse(discount);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<DiscountResponse> getAllDiscounts(Pageable pageable) {
        log.info("Fetching all discounts with pagination");

        Page<Discount> discounts = discountRepository.findAll(pageable);
        return discounts.map(discountMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DiscountResponse> getActiveDiscounts() {
        log.info("Fetching all active discounts");

        LocalDateTime now = LocalDateTime.now();
        List<Discount> discounts = discountRepository.findActiveDiscounts(CommonStatus.ACTIVE, now);

        return discounts.stream()
                .map(discountMapper::toResponse)
                .collect(Collectors.toList());
    }

    @Override
    @Transactional(readOnly = true)
    public DiscountResponse validateAndGetDiscount(String code, Double orderAmount) {
        log.info("Validating discount code: {} for order amount: {}", code, orderAmount);

        LocalDateTime now = LocalDateTime.now();
        Discount discount = discountRepository.findValidDiscountByCode(code, now)
            .orElseThrow(() -> new InvalidDataException(
                ErrorCode.DISCOUNT_CODE_INVALID,
                "Invalid or expired discount code: " + code
            ));

        // Check if discount has started
        if (discount.getStartAt().isAfter(now)) {
            throw new InvalidDataException(
                ErrorCode.DISCOUNT_NOT_STARTED,
                "Discount has not started yet"
            );
        }

        // Check if discount has expired
        if (discount.getEndAt().isBefore(now)) {
            throw new InvalidDataException(
                ErrorCode.DISCOUNT_EXPIRED,
                "Discount has expired"
            );
        }

        // Check usage limit
        if (discount.getUsageLimit() != null && discount.getOrders() != null) {
            if (discount.getOrders().size() >= discount.getUsageLimit()) {
                throw new InvalidDataException(
                    ErrorCode.DISCOUNT_USAGE_LIMIT_REACHED,
                    "Discount usage limit has been reached"
                );
            }
        }

        // Check minimum order amount
        if (discount.getMinOrderAmount() != null && orderAmount < discount.getMinOrderAmount()) {
            throw new InvalidDataException(
                ErrorCode.DISCOUNT_MIN_ORDER_NOT_MET,
                String.format("Minimum order amount of %.2f is required for this discount",
                    discount.getMinOrderAmount())
            );
        }

        log.info("Discount code validated successfully: {}", code);
        return discountMapper.toResponse(discount);
    }

    @Override
    public DiscountResponse updateStatus(Long id, CommonStatus status) {
        log.info("Updating discount status with id: {} to status: {}", id, status);

        Discount discount = discountRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.DISCOUNT_NOT_FOUND,
                "Discount not found with id: " + id
            ));

        discount.setStatus(status);
        Discount updatedDiscount = discountRepository.save(discount);

        log.info("Discount status updated successfully");
        return discountMapper.toResponse(updatedDiscount);
    }

    private void validateDateRange(LocalDateTime startAt, LocalDateTime endAt) {
        if (startAt.isAfter(endAt)) {
            throw new InvalidDataException(
                ErrorCode.INVALID_REQUEST,
                "Start date must be before end date"
            );
        }

        if (endAt.isBefore(LocalDateTime.now())) {
            throw new InvalidDataException(
                ErrorCode.INVALID_REQUEST,
                "End date cannot be in the past"
            );
        }
    }

    private void validateDiscountValue(DiscountRequest request) {
        // For percentage discount, value should be between 0 and 100
        if (request.getType() == vn.yame.common.enums.DiscountType.PERCENT) {
            if (request.getDiscountValue().doubleValue() > 100) {
                throw new InvalidDataException(
                    ErrorCode.INVALID_REQUEST,
                    "Percentage discount cannot exceed 100%"
                );
            }
        }
    }
}

