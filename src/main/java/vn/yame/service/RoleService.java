package vn.yame.service;

import vn.yame.common.enums.CommonStatus;
import vn.yame.dto.reponse.RoleResponse;
import vn.yame.dto.request.RoleRequest;

import java.util.List;

public interface RoleService {
    public RoleResponse createRole(RoleRequest roleRequest);

    public List<RoleResponse> findAllRoles();

    public RoleResponse findRoleById(Long id);

    public RoleResponse updateRole(Long id, RoleRequest roleRequest);

    public void deleteRole(Long id);

    public RoleResponse updateStatus(Long id, CommonStatus status);
}
