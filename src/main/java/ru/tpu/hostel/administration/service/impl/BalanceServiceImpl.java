package ru.tpu.hostel.administration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Recover;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.tpu.hostel.administration.dto.request.BalanceRequestDto;
import ru.tpu.hostel.administration.dto.response.BalanceResponseDto;
import ru.tpu.hostel.administration.dto.response.BalanceShortResponseDto;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.mapper.BalanceMapper;
import ru.tpu.hostel.administration.repository.BalanceRepository;
import ru.tpu.hostel.administration.service.BalanceService;
import ru.tpu.hostel.internal.exception.ServiceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    private static final String BALANCE_NOT_FOUND = "Баланс не найден";

    private static final String BALANCE_EDIT_EXCEPTION_MESSAGE = "Не удалось отредактировать баланс. Попробуйте позже";

    private final BalanceRepository balanceRepository;

    @Transactional
    @Override
    public BalanceResponseDto addBalance(BalanceRequestDto balanceRequestDto) {
        Balance balance = BalanceMapper.mapBalanceRequestDtoToBalance(balanceRequestDto);
        balanceRepository.save(balance);
        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Transactional
    @Override
    public BalanceResponseDto editBalance(BalanceRequestDto balanceRequestDto) {
        Balance balance = balanceRepository.findByIdOptimistic(balanceRequestDto.user())
                .orElse(null);

        if (balance == null) {
            balance = BalanceMapper.mapBalanceRequestDtoToBalance(balanceRequestDto);
            balanceRepository.save(balance);

            return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
        }

        balance.setBalance(balanceRequestDto.balance());
        try {
            balanceRepository.save(balance);
        } catch (ObjectOptimisticLockingFailureException e) {
            throw new ServiceException.Conflict(BALANCE_EDIT_EXCEPTION_MESSAGE);
        }

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Transactional
    @Override
    @Retryable(
            retryFor = ObjectOptimisticLockingFailureException.class,
            backoff = @Backoff(delay = 100, multiplier = 2),
            recover = "recoverEditBalanceWithAddingAmount"
    )
    public BalanceResponseDto editBalanceWithAddingAmount(BalanceRequestDto balanceRequestDto) {
        Balance balance = balanceRepository.findByIdOptimistic(balanceRequestDto.user())
                .orElse(null);

        if (balance == null) {
            balance = BalanceMapper.mapBalanceRequestDtoToBalance(balanceRequestDto);
            balanceRepository.save(balance);

            return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
        }

        balance.setBalance(balanceRequestDto.balance().add(balance.getBalance()));
        balanceRepository.save(balance);

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Recover
    public BalanceResponseDto recoverEditBalanceWithAddingAmount(
            ObjectOptimisticLockingFailureException e,
            BalanceRequestDto balanceRequestDto
    ) {
        throw new ServiceException.Conflict(BALANCE_EDIT_EXCEPTION_MESSAGE);
    }

    @Override
    public BalanceResponseDto getBalance(UUID userId) {
        Balance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new ServiceException.BadRequest(BALANCE_NOT_FOUND));

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Override
    public BalanceShortResponseDto getBalanceShort(UUID userId) {
        Balance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new ServiceException.BadRequest(BALANCE_NOT_FOUND));

        return BalanceMapper.mapBalanceToBalanceShortResponseDto(balance);
    }

    @Override
    public List<BalanceResponseDto> getAllBalances(int page, int size, Boolean negative, BigDecimal value) {
        Page<Balance> balances;

        Pageable pageable = PageRequest.of(page, size, Sort.by("user"));

        if (negative != null && value != null && negative) {
            balances = balanceRepository.findAllByBalanceLessThanEqual(value, pageable);
        } else if (negative != null && value != null) {
            balances = balanceRepository.findAllByBalanceGreaterThan(value, pageable);
        } else {
            balances = balanceRepository.findAll(pageable);
        }

        return balances.stream()
                .map(BalanceMapper::mapBalanceToBalanceResponseDto)
                .toList();
    }

    @Override
    public List<BalanceResponseDto> getAllBalancesByUsers(List<UUID> userIds) {
        return balanceRepository.findByUserInOrderByUser(userIds)
                .stream()
                .map(BalanceMapper::mapBalanceToBalanceResponseDto)
                .toList();
    }
}
