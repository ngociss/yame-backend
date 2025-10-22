package vn.yame.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ErrorCode;
import vn.yame.dto.request.MaterialRequest;
import vn.yame.dto.reponse.MaterialResponse;
import vn.yame.exception.ExistingResourcesException;
import vn.yame.exception.NotFoundResourcesException;
import vn.yame.mapper.MaterialMapper;
import vn.yame.model.Material;
import vn.yame.repository.MaterialRepository;
import vn.yame.service.MaterialService;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional
public class MaterialServiceImpl implements MaterialService {

    private final MaterialRepository materialRepository;
    private final MaterialMapper materialMapper;

    @Override
    public MaterialResponse createMaterial(MaterialRequest request) {
        log.info("Creating new material with name: {}", request.getName());

        // Check if name already exists
        if (materialRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.MATERIAL_NOT_FOUND,
                "Material with name '" + request.getName() + "' already exists"
            );
        }

        Material material = materialMapper.toEntity(request);
        material.setStatus(CommonStatus.ACTIVE);
        Material savedMaterial = materialRepository.save(material);

        log.info("Material created successfully with id: {}", savedMaterial.getId());
        return materialMapper.toResponse(savedMaterial);
    }

    @Override
    public MaterialResponse updateMaterial(Long id, MaterialRequest request) {
        log.info("Updating material with id: {}", id);

        Material material = materialRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.MATERIAL_NOT_FOUND,
                "Material not found with id: " + id
            ));

        // Check if name is being changed and if new name already exists
        if (!material.getName().equals(request.getName()) &&
            materialRepository.existsByName(request.getName())) {
            throw new ExistingResourcesException(
                ErrorCode.MATERIAL_NOT_FOUND,
                "Material with name '" + request.getName() + "' already exists"
            );
        }

        materialMapper.updateEntity(material, request);
        Material updatedMaterial = materialRepository.save(material);

        log.info("Material updated successfully with id: {}", id);
        return materialMapper.toResponse(updatedMaterial);
    }

    @Override
    public void deleteMaterial(Long id) {
        log.info("Soft deleting material with id: {}", id);

        Material material = materialRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.MATERIAL_NOT_FOUND,
                "Material not found with id: " + id
            ));

        // Check if material is being used by products (when product module is implemented)
        // if (material.getProducts() != null && !material.getProducts().isEmpty()) {
        //     throw new InvalidDataException(
        //         ErrorCode.MATERIAL_HAS_PRODUCTS,
        //         "Cannot delete material with existing products"
        //     );
        // }

        // Soft delete - set status to DELETED
        material.setStatus(CommonStatus.DELETED);
        materialRepository.save(material);

        log.info("Material soft deleted successfully with id: {}", id);
    }

    @Override
    @Transactional(readOnly = true)
    public MaterialResponse getMaterialById(Long id) {
        log.info("Fetching material with id: {}", id);

        Material material = materialRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.MATERIAL_NOT_FOUND,
                "Material not found with id: " + id
            ));

        return materialMapper.toResponse(material);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialResponse> getAllMaterials() {
        log.info("Fetching all materials");

        List<Material> materials = materialRepository.findAll();
        return materialMapper.toResponseList(materials);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<MaterialResponse> getAllMaterialsWithPagination(Pageable pageable) {
        log.info("Fetching materials with pagination - page: {}, size: {}",
            pageable.getPageNumber(), pageable.getPageSize());

        Page<Material> materialsPage = materialRepository.findAll(pageable);
        return materialsPage.map(materialMapper::toResponse);
    }

    @Override
    @Transactional(readOnly = true)
    public List<MaterialResponse> getActiveMaterials() {
        log.info("Fetching all active materials");

        List<Material> materials = materialRepository.findByStatus(CommonStatus.ACTIVE);
        return materialMapper.toResponseList(materials);
    }

    @Override
    public MaterialResponse updateStatus(Long id, CommonStatus status) {
        log.info("Updating status for material with id: {} to {}", id, status);

        Material material = materialRepository.findById(id)
            .orElseThrow(() -> new NotFoundResourcesException(
                ErrorCode.MATERIAL_NOT_FOUND,
                "Material not found with id: " + id
            ));

        material.setStatus(status);
        Material updatedMaterial = materialRepository.save(material);

        log.info("Material status updated successfully with id: {}", id);
        return materialMapper.toResponse(updatedMaterial);
    }
}

