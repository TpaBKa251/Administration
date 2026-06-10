package ru.tpu.hostel.administration.scheduler;

import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.repository.BalanceRepository;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceUpdateServiceTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OpenTelemetry openTelemetry;

    @InjectMocks
    private BalanceUpdateService balanceUpdateService;

    @Test
    void updateBalanceWithSuccess() {
        Balance balance = TestData.newBalance(TestData.USER_ID, TestData.BALANCE_VALUE);
        when(balanceRepository.findAllBalancesWithLock()).thenReturn(List.of(balance));

        balanceUpdateService.updateBalance();

        assertThat(balance.getBalance()).isEqualByComparingTo(TestData.BALANCE_VALUE.subtract(new BigDecimal(1000)));
        verify(balanceRepository).saveAll(List.of(balance));
    }
}
