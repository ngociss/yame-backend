package vn.yame.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yame.common.enums.CommonStatus;
import vn.yame.model.Resource;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    List<Resource> findByStatus(CommonStatus status);

    @Query("SELECT r FROM Resource r LEFT JOIN FETCH r.permissions WHERE r.id = :id")
    Optional<Resource> findByIdWithPermissions(@Param("id") Long id);

    @Query("SELECT r FROM Resource r LEFT JOIN FETCH r.permissions")
    List<Resource> findAllWithPermissions();

    // Fix N+1 query for pagination - use custom query with JOIN FETCH
    @Query(value = "SELECT DISTINCT r FROM Resource r LEFT JOIN FETCH r.permissions",
           countQuery = "SELECT COUNT(DISTINCT r) FROM Resource r")
    Page<Resource> findAllWithPermissions(Pageable pageable);

    // Methods to exclude DELETED resources
    @Query("SELECT r FROM Resource r LEFT JOIN FETCH r.permissions WHERE r.status <> 'DELETED'")
    List<Resource> findAllActiveWithPermissions();

    @Query("SELECT r FROM Resource r WHERE r.status <> 'DELETED' AND r.id = :id")
    Optional<Resource> findByIdExcludingDeleted(@Param("id") Long id);

    @Query("SELECT r FROM Resource r WHERE r.status <> 'DELETED' AND r.name = :name")
    Optional<Resource> findByNameExcludingDeleted(@Param("name") String name);
}
