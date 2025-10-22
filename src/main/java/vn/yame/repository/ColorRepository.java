package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yame.common.enums.CommonStatus;
import vn.yame.model.Color;

import java.util.List;

@Repository
public interface ColorRepository extends JpaRepository<Color, Long> {

    boolean existsByName(String name);

    boolean existsByHexCode(String hexCode);

    List<Color> findByStatus(CommonStatus status);
}

