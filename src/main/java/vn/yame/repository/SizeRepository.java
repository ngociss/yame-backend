package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yame.common.enums.CommonStatus;
import vn.yame.model.Size;

import java.util.List;

@Repository
public interface SizeRepository extends JpaRepository<Size, Long> {

    boolean existsByName(String name);

    List<Size> findByStatus(CommonStatus status);
}

