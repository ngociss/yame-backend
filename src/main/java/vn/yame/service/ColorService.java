package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.request.ColorRequest;
import vn.yame.dto.reponse.ColorResponse;

import java.util.List;

public interface ColorService {

    ColorResponse createColor(ColorRequest request);

    ColorResponse updateColor(Long id, ColorRequest request);

    void deleteColor(Long id);

    ColorResponse getColorById(Long id);

    List<ColorResponse> getAllColors();

    Page<ColorResponse> getAllColorsWithPagination(Pageable pageable);

    List<ColorResponse> getActiveColors();

    ColorResponse updateStatus(Long id, CommonStatus status);
}

