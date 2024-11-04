package ru.tpu.hostel.administration.service;

import ru.tpu.hostel.administration.dto.request.BalanceRequestDto;
import ru.tpu.hostel.administration.dto.response.BalanceResponseDto;
import ru.tpu.hostel.administration.dto.response.BalanceShortResponseDto;

import java.util.UUID;

public interface BalanceService {

    BalanceResponseDto addBalance(BalanceRequestDto balanceRequestDto);

    BalanceResponseDto editBalance(BalanceRequestDto balanceRequestDto);

    BalanceResponseDto editBalanceWithAddingAmount(BalanceRequestDto balanceRequestDto);

    BalanceResponseDto getBalance(UUID userId);

    BalanceShortResponseDto getBalanceShort(UUID userId);
}
