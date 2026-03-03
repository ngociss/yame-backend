package vn.yame.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yame.common.enums.CommonStatus;
import vn.yame.model.ShippingMethod;

import java.util.List;

@Repository
public interface ShippingMethodRepository extends JpaRepository<ShippingMethod, Long> {

    boolean existsByCorpName(String corpName);

    boolean existsByCorpNameAndIdNot(String corpName, Long id);

    List<ShippingMethod> findByStatus(CommonStatus status);

    Page<ShippingMethod> findByStatus(CommonStatus status, Pageable pageable);
}

