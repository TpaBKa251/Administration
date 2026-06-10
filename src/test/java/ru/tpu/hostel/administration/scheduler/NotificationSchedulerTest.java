package ru.tpu.hostel.administration.scheduler;

import io.opentelemetry.api.OpenTelemetry;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.external.rest.notification.NotificationClient;
import ru.tpu.hostel.administration.repository.BalanceRepository;
import ru.tpu.hostel.administration.repository.DocumentRepository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.lenient;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NotificationSchedulerTest {

    @Mock
    private BalanceRepository balanceRepository;

    @Mock
    private DocumentRepository documentRepository;

    @Mock
    private NotificationClient notificationClient;

    @Mock(answer = Answers.RETURNS_DEEP_STUBS)
    private OpenTelemetry openTelemetry;

    @InjectMocks
    private NotificationScheduler notificationScheduler;

    @Test
    void sendNotificationWithSuccess() {
        lenient().when(balanceRepository.findAllByBalanceLessThanEqual(any(BigDecimal.class)))
                .thenReturn(List.of(TestData.defaultBalance()));
        when(documentRepository.findAllByEndDateEquals(any(LocalDate.class)))
                .thenReturn(List.of(TestData.defaultDocument()));

        notificationScheduler.sendNotification();

        verify(notificationClient, atLeastOnce()).createNotification(any());
    }
}
