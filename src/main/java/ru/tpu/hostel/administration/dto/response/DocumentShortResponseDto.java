package ru.tpu.hostel.administration.dto.response;

import ru.tpu.hostel.administration.entity.DocumentType;

import java.time.LocalDate;

public record DocumentShortResponseDto(
        DocumentType type,
        LocalDate startDate,
        LocalDate endDate
) {
}
