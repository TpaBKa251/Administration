package ru.tpu.hostel.administration.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.tpu.hostel.administration.client.UserServiceClient;
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

    private final BalanceRepository balanceRepository;
    private final UserServiceClient userServiceClient;

    @Override
    public BalanceResponseDto addBalance(BalanceRequestDto balanceRequestDto) {
//        ResponseEntity<?> response;
//
//        try {
//            response = userServiceClient.getUserById(balanceRequestDto.user());
//        } catch (Exception e) {
//            throw new UserNotFound("Пользователь не найден");
//        }
//
//
//        if (!response.getStatusCode().is2xxSuccessful()) {
//            throw new UserNotFound("Пользователь не найден");
//        }

        Balance balance = BalanceMapper.mapBalanceRequestDtoToBalance(balanceRequestDto);

        balanceRepository.save(balance);

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Override
    public BalanceResponseDto editBalance(BalanceRequestDto balanceRequestDto) {
        Balance balance = balanceRepository.findById(balanceRequestDto.user())
                .orElseThrow(() -> new BalanceNotFound("Баланс не найден"));

        balance.setBalance(balanceRequestDto.balance());

        balanceRepository.save(balance);

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Override
    public BalanceResponseDto editBalanceWithAddingAmount(BalanceRequestDto balanceRequestDto) {
        Balance balance = balanceRepository.findById(balanceRequestDto.user())
                .orElseThrow(() -> new BalanceNotFound("Баланс не найден"));

        balance.setBalance(balanceRequestDto.balance().add(balance.getBalance()));

        balanceRepository.save(balance);

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Override
    public BalanceResponseDto getBalance(UUID userId) {
        Balance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new BalanceNotFound("Баланс не найден"));

        return BalanceMapper.mapBalanceToBalanceResponseDto(balance);
    }

    @Override
    public BalanceShortResponseDto getBalanceShort(UUID userId) {
        Balance balance = balanceRepository.findById(userId)
                .orElseThrow(() -> new BalanceNotFound("Баланс не найден"));

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
            throw new BalanceNotFound("Баланс не найден");
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
