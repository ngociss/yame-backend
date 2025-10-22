package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yame.common.enums.CommonStatus;
import vn.yame.model.Material;

import java.util.List;

@Repository
public interface MaterialRepository extends JpaRepository<Material, Long> {

    boolean existsByName(String name);

    List<Material> findByStatus(CommonStatus status);
}

