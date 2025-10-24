package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.request.SizeRequest;
import vn.yame.dto.reponse.SizeResponse;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.SizeMapper;
import vn.yame.model.Size;
import vn.yame.repository.SizeRepository;
import vn.yame.service.SizeService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class SizeServiceImpl implements SizeService {

    private final SizeRepository sizeRepository;
    private final SizeMapper sizeMapper;

    @Override
    public SizeResponse createSize(SizeRequest request) {
        log.info("Creating new size with name: {}", request.getName());

        // Check if name already exists
        if (sizeRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.SIZE_NOT_FOUND,
                "Size with name '" + request.getName() + "' already exists"
            );
        }

        Size size = sizeMapper.toEntity(request);
        size.setStatus(CommonStatus.ACTIVE);
        Size savedSize = sizeRepository.save(size);

        log.info("Size created successfully with id: {}", savedSize.getId());
        return sizeMapper.toResponse(savedSize);
    }

    @Override
    public SizeResponse updateSize(Long id, SizeRequest request) {
        log.info("Updating size with id: {}", id);

        Size size = sizeRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SIZE_NOT_FOUND,
                "Size not found with id: " + id
            ));

        // Check if name is being changed and if new name already exists
        if (!size.getName().equals(request.getName()) &&
            sizeRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.SIZE_NOT_FOUND,
                "Size with name '" + request.getName() + "' already exists"
            );
        }

        sizeMapper.updateEntity(size, request);
        Size updatedSize = sizeRepository.save(size);

        log.info("Size updated successfully with id: {}", id);
        return sizeMapper.toResponse(updatedSize);
    }

    @Override
    public void deleteSize(Long id) {
        log.info("Soft deleting size with id: {}", id);

        Size size = sizeRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SIZE_NOT_FOUND,
                "Size not found with id: " + id
            ));

        // Check if size is being used by product variants (when product variant module is implemented)
        // if (size.getProductVariants() != null && !size.getProductVariants().isEmpty()) {
        //     throw new InvalidDataException(
        //         ErrorCode.INVALID_REQUEST,
        //         "Cannot delete size with existing product variants"
        //     );
        // }

        // Soft delete - set status to DELETED
        size.setStatus(CommonStatus.DELETED);
        sizeRepository.save(size);

        log.info("Size soft deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public SizeResponse getSizeById(Long id) {
        log.info("Fetching size with id: {}", id);

        Size size = sizeRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SIZE_NOT_FOUND,
                "Size not found with id: " + id
            ));

        return sizeMapper.toResponse(size);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SizeResponse> getAllSizes() {
        log.info("Fetching all sizes");

        List<Size> sizes = sizeRepository.findAll();
        return sizeMapper.toResponseList(sizes);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<SizeResponse> getAllSizesWithPagination(Pageable pageable) {
        log.info("Fetching sizes with pagination - page: {}, size: {}",
            pageable.getPageNumber(), pageable.getPageSize());

        Page<Size> sizesPage = sizeRepository.findAll(pageable);
        return sizesPage.map(sizeMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<SizeResponse> getActiveSizes() {
        log.info("Fetching all active sizes");

        List<Size> sizes = sizeRepository.findByStatus(CommonStatus.ACTIVE);
        return sizeMapper.toResponseList(sizes);
    }

    @Override
    public SizeResponse updateStatus(Long id, CommonStatus status) {
        log.info("Updating status for size with id: {} to {}", id, status);

        Size size = sizeRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.SIZE_NOT_FOUND,
                "Size not found with id: " + id
            ));

        size.setStatus(status);
        Size updatedSize = sizeRepository.save(size);

        log.info("Size status updated successfully with id: {}", id);
        return sizeMapper.toResponse(updatedSize);
    }
}

