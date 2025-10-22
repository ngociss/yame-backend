package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yame.model.ProductGroup;

import java.util.List;

@Repository
public interface ProductGroupRepository extends JpaRepository<ProductGroup, Long> {

    boolean existsByName(String name);

    List<ProductGroup> findByNameContainingIgnoreCase(String name);
}

