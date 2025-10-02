package vn.yame.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yame.model.Permission;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PermissionRepository extends JpaRepository<Permission, Long> {

    Optional<Permission> findByCode(String code);

    Optional<Permission> findByName(String name);

    List<Permission> findByIsActiveTrue();

    Page<Permission> findByNameContainingIgnoreCase(String name, Pageable pageable);

    @Query("SELECT p FROM Permission p WHERE p.resource.id = :resourceId")
    List<Permission> findByResourceId(@Param("resourceId") Long resourceId);

    @Query("SELECT p FROM Permission p WHERE p.code IN :codes")
    List<Permission> findByCodes(@Param("codes") Set<String> codes);

    boolean existsByCode(String code);

    boolean existsByName(String name);

    @Query("SELECT p FROM Permission p LEFT JOIN FETCH p.resource WHERE p.id = :id")
    Optional<Permission> findByIdWithResource(@Param("id") Long id);
}

