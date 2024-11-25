package ru.tpu.hostel.administration.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import ru.tpu.hostel.administration.entity.Balance;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Repository
public interface BalanceRepository extends JpaRepository<Balance, UUID> {

    Page<Balance> findAllByBalanceLessThanEqual(BigDecimal balance, Pageable pageable);

    Page<Balance> findAllByBalanceGreaterThan(BigDecimal balance, Pageable pageable);

    List<Balance> findByUserInOrderByUser(List<UUID> ids);
}
