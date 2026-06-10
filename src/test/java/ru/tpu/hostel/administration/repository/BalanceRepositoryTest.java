package ru.tpu.hostel.administration.repository;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.repository.util.RepositoryTest;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@RepositoryTest
class BalanceRepositoryTest {

    @Autowired
    private BalanceRepository balanceRepository;

    @BeforeEach
    void setUp() {
        balanceRepository.deleteAll();
        balanceRepository.save(TestData.newBalance(TestData.USER_ID, TestData.BALANCE_VALUE));
        balanceRepository.save(TestData.newBalance(TestData.OTHER_USER_ID, TestData.THRESHOLD_VALUE));
    }

    @Test
    void findByIdOptimisticWhenExists() {
        Optional<Balance> result = balanceRepository.findByIdOptimistic(TestData.USER_ID);

        assertThat(result).isPresent();
        assertThat(result.get().getBalance()).isEqualByComparingTo(TestData.BALANCE_VALUE);
    }

    @Test
    void findByIdOptimisticWhenNotExists() {
        Optional<Balance> result = balanceRepository.findByIdOptimistic(TestData.DOCUMENT_ID);

        assertThat(result).isEmpty();
    }

    @Test
    void findAllByBalanceLessThanEqualWithSuccess() {
        List<Balance> result = balanceRepository.findAllByBalanceLessThanEqual(TestData.THRESHOLD_VALUE);

        assertThat(result).extracting(Balance::getUser).containsExactly(TestData.OTHER_USER_ID);
    }

    @Test
    void findAllByBalanceGreaterThanWithSuccess() {
        Pageable pageable = PageRequest.of(TestData.PAGE, TestData.SIZE);

        var result = balanceRepository.findAllByBalanceGreaterThan(TestData.THRESHOLD_VALUE, pageable);

        assertThat(result.getContent()).extracting(Balance::getUser).containsExactly(TestData.USER_ID);
    }

    @Test
    void findByUserInOrderByUserWithSuccess() {
        List<Balance> result =
                balanceRepository.findByUserInOrderByUser(List.of(TestData.USER_ID, TestData.OTHER_USER_ID));

        assertThat(result).hasSize(2);
    }

    @Test
    void findAllBalancesWithLockWithSuccess() {
        List<Balance> result = balanceRepository.findAllBalancesWithLock();

        assertThat(result).hasSize(2);
    }
}
