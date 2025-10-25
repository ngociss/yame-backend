package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import vn.yame.model.ProductImage;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

    List<ProductImage> findByProductIdOrderByDisplayOrderAsc(Long productId);


    @Query("select pi from ProductImage pi where pi.product.id = :productId and pi.isPrimary = true")
    List<ProductImage> findByProductIdAndIsPrimaryTrue(Long productId);

    boolean existsByProductId(Long productId);

    void deleteByProductId(Long productId);
}

