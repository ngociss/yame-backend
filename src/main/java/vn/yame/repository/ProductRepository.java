package vn.yame.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yame.common.enums.CommonStatus;
import vn.yame.common.enums.ProductStatus;
import vn.yame.model.Product;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    boolean existsBySlug(String slug);

    Optional<Product> findBySlug(String slug);

    boolean existsByName(String name);

    List<Product> findByStatus(CommonStatus status);

    List<Product> findByProductStatus(ProductStatus productStatus);

    Page<Product> findByCategoryId(Long categoryId, Pageable pageable);

    Page<Product> findByMaterialId(Long materialId, Pageable pageable);

    @Query("SELECT p FROM Product p WHERE " +
           "(:categoryId IS NULL OR p.category.id = :categoryId) AND " +
           "(:materialId IS NULL OR p.material.id = :materialId) AND " +
           "(:status IS NULL OR p.status = :status) AND " +
           "(:productStatus IS NULL OR p.productStatus = :productStatus) AND " +
           "(:name IS NULL OR LOWER(p.name) LIKE LOWER(CONCAT('%', :name, '%')))")
    Page<Product> searchProducts(
        @Param("categoryId") Long categoryId,
        @Param("materialId") Long materialId,
        @Param("status") CommonStatus status,
        @Param("productStatus") ProductStatus productStatus,
        @Param("name") String name,
        Pageable pageable
    );
}

