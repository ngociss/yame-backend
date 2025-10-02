package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yame.model.Resource;

import java.util.List;
import java.util.Optional;

@Repository
public interface ResourceRepository extends JpaRepository<Resource, Long> {

    Optional<Resource> findByName(String name);

    boolean existsByName(String name);

    boolean existsByNameAndIdNot(String name, Long id);

    @Query("SELECT r FROM Resource r LEFT JOIN FETCH r.permissions WHERE r.id = :id")
    Optional<Resource> findByIdWithPermissions(@Param("id") Long id);

    @Query("SELECT r FROM Resource r LEFT JOIN FETCH r.permissions")
    List<Resource> findAllWithPermissions();
}
