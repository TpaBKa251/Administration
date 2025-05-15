package ru.tpu.hostel.administration.repository;

import jakarta.persistence.LockModeType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.administration.entity.Balance;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {

    Page<Balance> findAllByBalanceLessThanEqual(BigDecimal balance, Pageable pageable);

    Page<Balance> findAllByBalanceGreaterThan(BigDecimal balance, Pageable pageable);

    List<Balance> findByUserInOrderByUser(List<UUID> ids);

    List<Balance> findAllByBalanceLessThanEqual(BigDecimal balance);

    @Transactional
    @Query("SELECT b FROM Balance b WHERE b.user = :id")
    @Lock(LockModeType.OPTIMISTIC)
    Optional<Balance> findByIdOptimistic(@Param("id") UUID id);

    @Transactional
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT b FROM Balance b")
    List<Balance> findAllBalancesWithLock();
}
