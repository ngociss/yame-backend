package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.reponse.MaterialResponse;
import vn.yame.dto.request.MaterialRequest;

import java.util.List;

public interface MaterialService {

    MaterialResponse createMaterial(MaterialRequest request);

    MaterialResponse updateMaterial(Long id, MaterialRequest request);

    void deleteMaterial(Long id);

    MaterialResponse getMaterialById(Long id);

    List<MaterialResponse> getAllMaterials();

    Page<MaterialResponse> getAllMaterialsWithPagination(Pageable pageable);

    List<MaterialResponse> getActiveMaterials();

    MaterialResponse updateStatus(Long id, CommonStatus status);
}


