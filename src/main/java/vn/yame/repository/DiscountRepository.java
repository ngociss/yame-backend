package vn.yame.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import vn.yame.common.enums.CommonStatus;
import vn.yame.model.Discount;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {

    Optional<Discount> findByCodeAndStatus(String code, CommonStatus status);

    Optional<Discount> findByCode(String code);

    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    List<Discount> findByStatus(CommonStatus status);

    @Query("SELECT d FROM Discount d WHERE d.status = :status " +
           "AND d.startAt <= :now AND d.endAt >= :now")
    List<Discount> findActiveDiscounts(@Param("status") CommonStatus status,
                                       @Param("now") LocalDateTime now);

    @Query("SELECT d FROM Discount d WHERE d.code = :code " +
           "AND d.status = 'ACTIVE' " +
           "AND d.startAt <= :now AND d.endAt >= :now")
    Optional<Discount> findValidDiscountByCode(@Param("code") String code,
                                                @Param("now") LocalDateTime now);
}

