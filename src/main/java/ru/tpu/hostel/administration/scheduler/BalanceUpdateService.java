package ru.tpu.hostel.administration.scheduler;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.repository.BalanceRepository;
import ru.tpu.hostel.internal.common.logging.LogFilter;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceUpdateService {

    private BigDecimal cost = new BigDecimal(1000);

    private final BalanceRepository balanceRepository;

    @Scheduled(cron = "0 0 0 L * ?", zone = "Asia/Tomsk")
    @LogFilter(enableResultLogging = false)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateBalance() {
        List<Balance> balances = balanceRepository.findAllBalancesWithLock();
        balances.forEach(balance -> balance.setBalance(balance.getBalance().subtract(cost)));
        balanceRepository.saveAll(balances);
    }
}
