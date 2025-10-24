package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.request.SizeRequest;
import vn.yame.dto.reponse.SizeResponse;

import java.util.List;

public interface SizeService {

    SizeResponse createSize(SizeRequest request);

    SizeResponse updateSize(Long id, SizeRequest request);

    void deleteSize(Long id);

    SizeResponse getSizeById(Long id);

    List<SizeResponse> getAllSizes();

    Page<SizeResponse> getAllSizesWithPagination(Pageable pageable);

    List<SizeResponse> getActiveSizes();

    SizeResponse updateStatus(Long id, CommonStatus status);
}

