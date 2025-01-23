package ru.tpu.hostel.administration.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.repository.BalanceRepository;

import java.math.BigDecimal;
import java.util.List;

@Service
@EnableScheduling
@RequiredArgsConstructor
public class BalanceUpdateService {

    private BigDecimal cost = new BigDecimal(1000);

    private final BalanceRepository balanceRepository;

    @Scheduled(cron = "0 0 0 L * ?", zone = "Asia/Tomsk")
    public void updateBalance() {
        List<Balance> balances = balanceRepository.findAll();

        for (Balance balance : balances) {
            balance.setBalance(balance.getBalance().subtract(cost));
            balanceRepository.save(balance);
        }
    }
}
