package ru.tpu.hostel.administration.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.administration.external.rest.notification.NotificationClient;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.entity.Document;
import ru.tpu.hostel.administration.external.rest.notification.dto.NotificationRequestDto;
import ru.tpu.hostel.administration.external.rest.notification.dto.NotificationType;
import ru.tpu.hostel.administration.repository.BalanceRepository;
import ru.tpu.hostel.administration.repository.DocumentRepository;
import ru.tpu.hostel.internal.common.logging.LogFilter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationScheduler {

    private static final int WEEK_IN_DAYS = 7;
    private static final int WEEK_IN_DAYS_PLUS_ONE = 8;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final int ONE_DAY = 1;
    private static final int TWO_DAYS = 2;
    private static final BigDecimal BALANCE_TO_SEND_NOTIFICATION = new BigDecimal("1000");

    private final BalanceRepository balanceRepository;
    private final DocumentRepository documentRepository;

    private final NotificationClient notificationClient;

    @Scheduled(cron = "0 0 10 * * *", zone = "Asia/Tomsk")
    @LogFilter(enableResultLogging = false)
    public void sendNotification() {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Tomsk"));
        LocalDate lastDayOfMonth = today.withDayOfMonth(today.lengthOfMonth());
        LocalDate weekBeforeEnd = lastDayOfMonth.minusDays(WEEK_IN_DAYS);

        if (today.isEqual(weekBeforeEnd) || today.isEqual(lastDayOfMonth.minusDays(ONE_DAY))) {
            sendNotificationAboutBalance(lastDayOfMonth);
        }

        LocalDate dayForDocuments = today.plusDays(WEEK_IN_DAYS_PLUS_ONE);
        sendNotificationAboutDocument(dayForDocuments);

        dayForDocuments = today.plusDays(ONE_DAY);
        sendNotificationAboutDocument(dayForDocuments);
    }

    private void sendNotificationAboutBalance(LocalDate lastDayOfMonth) {
        List<Balance> balances = balanceRepository.findAllByBalanceLessThanEqual(BALANCE_TO_SEND_NOTIFICATION);

        for (Balance balance : balances) {
            try {
                NotificationRequestDto notification = new NotificationRequestDto(
                        balance.getUser(),
                        NotificationType.BALANCE,
                        "Пополните баланс общежития",
                        "Ваш баланс составляет " + balance.getBalance()
                                + ". Его необходимо пополнить "
                                + selectDateMessage(lastDayOfMonth, true)
                );

                notificationClient.createNotification(notification);
            } catch (Exception e) {
                logError(balance.getUser(), e);
            }
        }
    }

    private void sendNotificationAboutDocument(LocalDate date) {
        List<Document> documents = documentRepository.findAllByEndDateEquals(date);

        for (Document document : documents) {
            try {
                NotificationRequestDto notification = new NotificationRequestDto(
                        document.getUser(),
                        NotificationType.DOCUMENT,
                        "Обновите документ \"" + document.getType().getDocumentName() + "\"",
                        document.getType().getDocumentName()
                                + " заканчивается "
                                + selectDateMessage(date, false)
                                + ". Обновите справку "
                                + selectDateMessage(date.minusDays(ONE_DAY), true)
                );

                notificationClient.createNotification(notification);
            } catch (Exception e) {
                logError(document.getUser(), e);
            }
        }
    }

    private void logError(UUID userId, Exception e) {
        log.error(
                "Уведомление для пользователя {} не было отправлено, но возможно было сохранено: {}",
                userId,
                e.getMessage(),
                e
        );
    }

    private String selectDateMessage(LocalDate date, boolean expDate) {
        LocalDate today = LocalDate.now(ZoneId.of("Asia/Tomsk"));
        String expirationDate = date.format(DATE_FORMATTER);

        if (today.isEqual(date)) {
            return "сегодня";
        }
        if (today.plusDays(ONE_DAY).isEqual(date)) {
            return expDate
                    ? "до завтра (" + expirationDate + ") включительно"
                    : "завтра (" + expirationDate + ")";
        }
        if (today.plusDays(TWO_DAYS).isEqual(date)) {
            return expDate
                    ? "до послезавтра (" + expirationDate + ") включительно"
                    : "послезавтра (" + expirationDate + ")";
        }

        return expDate
                ? "до " + expirationDate + " включительно"
                : expirationDate;
    }
}
