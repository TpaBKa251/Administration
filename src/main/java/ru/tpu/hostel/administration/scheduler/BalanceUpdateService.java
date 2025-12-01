package ru.tpu.hostel.administration.scheduler;

import io.opentelemetry.api.OpenTelemetry;
import io.opentelemetry.api.trace.Span;
import io.opentelemetry.api.trace.SpanContext;
import io.opentelemetry.api.trace.SpanKind;
import io.opentelemetry.api.trace.StatusCode;
import io.opentelemetry.context.Scope;
import lombok.RequiredArgsConstructor;
import org.slf4j.MDC;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.repository.BalanceRepository;
import ru.tpu.hostel.internal.utils.LogFilter;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
public class BalanceUpdateService {

    private static final String SCOPE_NAME = "ru.tpu.hostel.administration.scheduler.balance.update";

    private BigDecimal cost = new BigDecimal(1000);

    private final BalanceRepository balanceRepository;

    private final OpenTelemetry openTelemetry;

    @Scheduled(cron = "0 0 0 L * ?", zone = "Asia/Tomsk")
    @LogFilter(enableResultLogging = false)
    @Transactional(isolation = Isolation.SERIALIZABLE)
    public void updateBalance() {
        Span span = openTelemetry.getTracer(SCOPE_NAME)
                .spanBuilder("Update balance")
                .setSpanKind(SpanKind.INTERNAL)
                .startSpan();

        try (Scope ignored = span.makeCurrent()) {
            SpanContext context = span.getSpanContext();
            MDC.put("traceId", context.getTraceId());
            MDC.put("spanId", context.getSpanId());

            List<Balance> balances = balanceRepository.findAllBalancesWithLock();
            balances.forEach(balance -> balance.setBalance(balance.getBalance().subtract(cost)));
            balanceRepository.saveAll(balances);

            span.setStatus(StatusCode.OK);
        } catch (Exception e) {
            span.recordException(e);
            span.setStatus(StatusCode.ERROR);
        } finally {
            MDC.clear();
            span.end();
        }
    }
}
