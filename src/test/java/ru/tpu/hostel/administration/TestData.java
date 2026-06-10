package ru.tpu.hostel.administration;

import ru.tpu.hostel.administration.dto.request.BalanceRequestDto;
import ru.tpu.hostel.administration.dto.request.DocumentEditRequestDto;
import ru.tpu.hostel.administration.dto.request.DocumentRequestDto;
import ru.tpu.hostel.administration.dto.response.BalanceResponseDto;
import ru.tpu.hostel.administration.dto.response.BalanceShortResponseDto;
import ru.tpu.hostel.administration.dto.response.DocumentResponseDto;
import ru.tpu.hostel.administration.dto.response.DocumentShortResponseDto;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.entity.DocumentType;
import ru.tpu.hostel.administration.external.rest.notification.dto.NotificationRequestDto;
import ru.tpu.hostel.administration.external.rest.notification.dto.NotificationType;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public final class TestData {

    public static final UUID USER_ID = UUID.fromString("11111111-1111-1111-1111-111111111111");

    public static final UUID OTHER_USER_ID = UUID.fromString("22222222-2222-2222-2222-222222222222");

    public static final UUID DOCUMENT_ID = UUID.fromString("33333333-3333-3333-3333-333333333333");

    public static final UUID OTHER_DOCUMENT_ID = UUID.fromString("44444444-4444-4444-4444-444444444444");

    public static final BigDecimal BALANCE_VALUE = new BigDecimal("5000");

    public static final BigDecimal NEGATIVE_BALANCE_VALUE = new BigDecimal("-1000");

    public static final BigDecimal ADD_AMOUNT = new BigDecimal("1500");

    public static final BigDecimal THRESHOLD_VALUE = new BigDecimal("1000");

    public static final LocalDate START_DATE = LocalDate.of(2026, 1, 1);

    public static final LocalDate END_DATE = LocalDate.of(2026, 12, 31);

    public static final LocalDate FUTURE_END_DATE = LocalDate.of(2030, 12, 31);

    public static final int PAGE = 0;

    public static final int SIZE = 10;

    public static final String NOTIFICATION_TITLE = "Заголовок";

    public static final String NOTIFICATION_MESSAGE = "Сообщение";

    private TestData() {
    }

    public static Balance newBalance(UUID user, BigDecimal value) {
        Balance balance = new Balance();
        balance.setUser(user);
        balance.setBalance(value);
        balance.setVersion(0L);
        return balance;
    }

    public static Balance defaultBalance() {
        return newBalance(USER_ID, BALANCE_VALUE);
    }

    public static Document newDocument(
            UUID id,
            UUID user,
            DocumentType type,
            LocalDate startDate,
            LocalDate endDate
    ) {
        Document document = new Document();
        document.setId(id);
        document.setUser(user);
        document.setType(type);
        document.setStartDate(startDate);
        document.setEndDate(endDate);
        document.setVersion(0L);
        return document;
    }

    public static Document defaultDocument() {
        return newDocument(DOCUMENT_ID, USER_ID, DocumentType.FLUOROGRAPHY, START_DATE, END_DATE);
    }

    public static BalanceRequestDto balanceRequestDto(UUID user, BigDecimal value) {
        return new BalanceRequestDto(user, value);
    }

    public static BalanceRequestDto defaultBalanceRequestDto() {
        return balanceRequestDto(USER_ID, BALANCE_VALUE);
    }

    public static BalanceResponseDto balanceResponseDto() {
        return new BalanceResponseDto(USER_ID, BALANCE_VALUE);
    }

    public static BalanceShortResponseDto balanceShortResponseDto() {
        return new BalanceShortResponseDto(BALANCE_VALUE);
    }

    public static DocumentRequestDto documentRequestDto(UUID user, DocumentType type) {
        return new DocumentRequestDto(user, type, START_DATE, END_DATE);
    }

    public static DocumentRequestDto defaultDocumentRequestDto() {
        return documentRequestDto(USER_ID, DocumentType.FLUOROGRAPHY);
    }

    public static DocumentEditRequestDto documentEditRequestDto(UUID id) {
        return new DocumentEditRequestDto(id, START_DATE, FUTURE_END_DATE);
    }

    public static DocumentResponseDto documentResponseDto() {
        return new DocumentResponseDto(
                DOCUMENT_ID,
                USER_ID,
                DocumentType.FLUOROGRAPHY,
                START_DATE,
                END_DATE
        );
    }

    public static DocumentShortResponseDto documentShortResponseDto() {
        return new DocumentShortResponseDto(DocumentType.FLUOROGRAPHY, START_DATE, END_DATE);
    }

    public static NotificationRequestDto notificationRequestDto() {
        return new NotificationRequestDto(
                USER_ID,
                NotificationType.BALANCE,
                NOTIFICATION_TITLE,
                NOTIFICATION_MESSAGE
        );
    }
}
