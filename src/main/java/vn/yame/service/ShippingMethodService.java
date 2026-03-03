package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.request.ShippingMethodRequest;
import vn.yame.dto.reponse.ShippingMethodResponse;

import java.util.List;

public interface ShippingMethodService {

    ShippingMethodResponse createShippingMethod(ShippingMethodRequest request);

    ShippingMethodResponse updateShippingMethod(Long id, ShippingMethodRequest request);

    void deleteShippingMethod(Long id);

    ShippingMethodResponse getShippingMethodById(Long id);

    Page<ShippingMethodResponse> getAllShippingMethods(Pageable pageable);

    List<ShippingMethodResponse> getActiveShippingMethods();

    ShippingMethodResponse updateStatus(Long id, CommonStatus status);
}

