package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.reponse.DiscountResponse;
import vn.yame.dto.request.DiscountRequest;

import java.util.List;

public interface DiscountService {

    DiscountResponse createDiscount(DiscountRequest request);

    DiscountResponse updateDiscount(Long id, DiscountRequest request);

    void deleteDiscount(Long id);

    DiscountResponse getDiscountById(Long id);

    DiscountResponse getDiscountByCode(String code);

    Page<DiscountResponse> getAllDiscounts(Pageable pageable);

    List<DiscountResponse> getActiveDiscounts();

    DiscountResponse validateAndGetDiscount(String code, Double orderAmount);

    DiscountResponse updateStatus(Long id, CommonStatus status);
}

