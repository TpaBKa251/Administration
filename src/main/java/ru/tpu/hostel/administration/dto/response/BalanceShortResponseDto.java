package ru.tpu.hostel.administration.dto.response;

import java.math.BigDecimal;

public record BalanceShortResponseDto(
        BigDecimal balance
) {
}
