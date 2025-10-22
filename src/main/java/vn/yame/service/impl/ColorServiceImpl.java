package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.request.ColorRequest;
import vn.yame.dto.reponse.ColorResponse;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.ColorMapper;
import vn.yame.model.Color;
import vn.yame.repository.ColorRepository;
import vn.yame.service.ColorService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class ColorServiceImpl implements ColorService {

    private final ColorRepository colorRepository;
    private final ColorMapper colorMapper;

    @Override
    public ColorResponse createColor(ColorRequest request) {
        log.info("Creating new color with name: {}", request.getName());

        // Check if name already exists
        if (colorRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color with name '" + request.getName() + "' already exists"
            );
        }

        // Check if hex code already exists
        if (colorRepository.existsByHexCode(request.getHexCode())) {
            throw new ExistingResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color with hex code '" + request.getHexCode() + "' already exists"
            );
        }

        Color color = colorMapper.toEntity(request);
        color.setStatus(CommonStatus.ACTIVE);
        Color savedColor = colorRepository.save(color);

        log.info("Color created successfully with id: {}", savedColor.getId());
        return colorMapper.toResponse(savedColor);
    }

    @Override
    public ColorResponse updateColor(Long id, ColorRequest request) {
        log.info("Updating color with id: {}", id);

        Color color = colorRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color not found with id: " + id
            ));

        // Check if name is being changed and if new name already exists
        if (!color.getName().equals(request.getName()) &&
            colorRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color with name '" + request.getName() + "' already exists"
            );
        }

        // Check if hex code is being changed and if new hex code already exists
        if (!color.getHexCode().equals(request.getHexCode()) &&
            colorRepository.existsByHexCode(request.getHexCode())) {
            throw new ExistingResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color with hex code '" + request.getHexCode() + "' already exists"
            );
        }

        colorMapper.updateEntity(color, request);
        Color updatedColor = colorRepository.save(color);

        log.info("Color updated successfully with id: {}", id);
        return colorMapper.toResponse(updatedColor);
    }

    @Override
    public void deleteColor(Long id) {
        log.info("Soft deleting color with id: {}", id);

        Color color = colorRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color not found with id: " + id
            ));

        // Check if color is being used by product variants (when product variant module is implemented)
        // if (color.getProductVariants() != null && !color.getProductVariants().isEmpty()) {
        //     throw new InvalidDataException(
        //         ErrorCode.INVALID_REQUEST,
        //         "Cannot delete color with existing product variants"
        //     );
        // }

        // Soft delete - set status to DELETED
        color.setStatus(CommonStatus.DELETED);
        colorRepository.save(color);

        log.info("Color soft deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public ColorResponse getColorById(Long id) {
        log.info("Fetching color with id: {}", id);

        Color color = colorRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color not found with id: " + id
            ));

        return colorMapper.toResponse(color);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ColorResponse> getAllColors() {
        log.info("Fetching all colors");

        List<Color> colors = colorRepository.findAll();
        return colorMapper.toResponseList(colors);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<ColorResponse> getAllColorsWithPagination(Pageable pageable) {
        log.info("Fetching colors with pagination - page: {}, size: {}",
            pageable.getPageNumber(), pageable.getPageSize());

        Page<Color> colorsPage = colorRepository.findAll(pageable);
        return colorsPage.map(colorMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ColorResponse> getActiveColors() {
        log.info("Fetching all active colors");

        List<Color> colors = colorRepository.findByStatus(CommonStatus.ACTIVE);
        return colorMapper.toResponseList(colors);
    }

    @Override
    public ColorResponse updateStatus(Long id, CommonStatus status) {
        log.info("Updating status for color with id: {} to {}", id, status);

        Color color = colorRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.COLOR_NOT_FOUND,
                "Color not found with id: " + id
            ));

        color.setStatus(status);
        Color updatedColor = colorRepository.save(color);

        log.info("Color status updated successfully with id: {}", id);
        return colorMapper.toResponse(updatedColor);
    }
}

