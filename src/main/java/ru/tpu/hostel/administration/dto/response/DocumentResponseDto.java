package ru.tpu.hostel.administration.dto.response;

import ru.tpu.hostel.administration.entity.DocumentType;

import java.time.LocalDate;
import java.util.UUID;

public record DocumentResponseDto(
        UUID id,
        UUID user,
        DocumentType type,
        LocalDate startDate,
        LocalDate endDate
) {
}
