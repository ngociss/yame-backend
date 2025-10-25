package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yame.common.enums.CommonStatus;
import vn.yame.model.ProductVariant;

import java.util.List;

@Repository
public interface ProductVariantRepository extends JpaRepository<ProductVariant, Long> {

    boolean existsBySkuCode(String skuCode);

    boolean existsBySkuCodeAndIdNot(String skuCode, Long id);

    List<ProductVariant> findByStatus(CommonStatus status);

    List<ProductVariant> findByProductId(Long productId);

    List<ProductVariant> findByProductIdAndStatus(Long productId, CommonStatus status);

    boolean existsByColorId(Long colorId);

    boolean existsBySizeId(Long sizeId);

    boolean existsByProductId(Long productId);
}

