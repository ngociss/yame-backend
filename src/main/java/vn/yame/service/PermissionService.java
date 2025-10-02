package vn.yame.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import vn.yame.dto.reponse.PermissionResponse;
import vn.yame.dto.request.PermissionRequest;

import java.util.List;
import java.util.Set;

public interface PermissionService {

    PermissionResponse create(PermissionRequest request);

    PermissionResponse update(Long id, PermissionRequest request);

    PermissionResponse findById(Long id);

    PermissionResponse findByCode(String code);

    Page<PermissionResponse> findAll(Pageable pageable);

    Page<PermissionResponse> searchByName(String name, Pageable pageable);

    List<PermissionResponse> findByResourceId(Long resourceId);

    List<PermissionResponse> findActivePermissions();

    List<PermissionResponse> findByCodes(Set<String> codes);

    void delete(Long id);

    void toggleActive(Long id);

    boolean existsByCode(String code);

    boolean existsByName(String name);
}
