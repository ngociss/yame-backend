package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import vn.yame.common.enums.CommonStatus;
import vn.yame.model.Category;

import java.util.List;
import java.util.Optional;

@Repository
public interface CategoryRepository extends JpaRepository<Category, Long> {

    boolean existsBySlug(String slug);

    Optional<Category> findBySlug(String slug);

    boolean existsByName(String name);

    List<Category> findByStatus(CommonStatus status);
}
