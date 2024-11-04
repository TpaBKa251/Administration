package ru.tpu.hostel.administration.mapper;

import org.springframework.stereotype.Component;
import ru.tpu.hostel.administration.dto.request.BalanceRequestDto;
import ru.tpu.hostel.administration.dto.response.BalanceResponseDto;
import ru.tpu.hostel.administration.dto.response.BalanceShortResponseDto;
import ru.tpu.hostel.administration.entity.Balance;

@Component
public class BalanceMapper {

    public static BalanceResponseDto mapBalanceToBalanceResponseDto(Balance balance) {
        return new BalanceResponseDto(
                balance.getUser(),
                balance.getBalance()
        );
    }

    public static BalanceShortResponseDto mapBalanceToBalanceShortResponseDto(Balance balance) {
        return new BalanceShortResponseDto(
                balance.getBalance()
        );
    }

    public static Balance mapBalanceRequestDtoToBalance(BalanceRequestDto balanceRequestDto) {
        Balance balance = new Balance();
        balance.setUser(balanceRequestDto.user());
        balance.setBalance(balanceRequestDto.balance());

        return balance;
    }
}
