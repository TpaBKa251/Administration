package ru.tpu.hostel.administration.dto.response;

import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record BalanceResponseDto(
        UUID user,
        BigDecimal balance
) {
}
