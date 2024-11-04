package ru.tpu.hostel.administration.dto.response;

import ru.tpu.hostel.administration.enums.DocumentType;

import java.time.LocalDate;
import java.util.UUID;

public record DocumentShortResponseDto(
        DocumentType type,
        LocalDate startDate,
        LocalDate endDate
) {
}
