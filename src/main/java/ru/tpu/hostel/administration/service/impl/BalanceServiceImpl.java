package ru.tpu.hostel.administration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.administration.common.logging.LogFilter;
import ru.tpu.hostel.administration.dto.request.BalanceRequestDto;
import ru.tpu.hostel.administration.dto.response.BalanceResponseDto;
import ru.tpu.hostel.administration.dto.response.BalanceShortResponseDto;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.exception.BalanceNotFound;
import ru.tpu.hostel.administration.mapper.BalanceMapper;
import ru.tpu.hostel.administration.repository.BalanceRepository;
import ru.tpu.hostel.administration.service.BalanceService;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BalanceServiceImpl implements BalanceService {

    public static final String BALANCE_NOT_FOUND = "Баланс не найден";
    private final BalanceRepository balanceRepository;

    @Override
    public BalanceResponseDto addBalance(BalanceRequestDto balanceRequestDto) {

        Balance balance = BalanceMapper.mapBalanceRequestDtoToBalance(balanceRequestDto);

        balanceRepository.save(balance);

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Override
    public BalanceResponseDto editBalance(BalanceRequestDto balanceRequestDto) {
        Balance balance = balanceRepository.findById(balanceRequestDto.user())
                .orElse(null);

        if (balance == null) {
            balance = BalanceMapper.mapBalanceRequestDtoToBalance(balanceRequestDto);
            balanceRepository.save(balance);

            return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
        }

        balance.setBalance(balanceRequestDto.balance());
        balanceRepository.save(balance);

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Override
    public BalanceResponseDto editBalanceWithAddingAmount(BalanceRequestDto balanceRequestDto) {
        Balance balance = balanceRepository.findById(balanceRequestDto.user())
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

    @LogFilter(enableParamsLogging = false)
    @Override
    public BalanceResponseDto getBalance(UUID userId) {
        Balance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new BalanceNotFound(BALANCE_NOT_FOUND));

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @LogFilter(enableParamsLogging = false)
    @Override
    public BalanceShortResponseDto getBalanceShort(UUID userId) {
        Balance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new BalanceNotFound(BALANCE_NOT_FOUND));

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

        if (balances.isEmpty()) {
            throw new BalanceNotFound(BALANCE_NOT_FOUND);
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
