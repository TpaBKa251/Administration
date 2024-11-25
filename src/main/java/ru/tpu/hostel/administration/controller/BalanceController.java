package ru.tpu.hostel.administration.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.tpu.hostel.administration.dto.request.BalanceRequestDto;
import ru.tpu.hostel.administration.dto.response.BalanceResponseDto;
import ru.tpu.hostel.administration.dto.response.BalanceShortResponseDto;
import ru.tpu.hostel.administration.service.BalanceService;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("balance")
@RequiredArgsConstructor
public class BalanceController {

    private final BalanceService balanceService;

    @PostMapping
    public BalanceResponseDto addBalance(@RequestBody @Valid BalanceRequestDto balanceRequestDto) {
        return balanceService.addBalance(balanceRequestDto);
    }

    @PatchMapping("/edit")
    public BalanceResponseDto editBalance(@RequestBody @Valid BalanceRequestDto balanceRequestDto) {
        return balanceService.editBalance(balanceRequestDto);
    }

    @PatchMapping("/edit/adding")
    public BalanceResponseDto editAddBalance(@RequestBody @Valid BalanceRequestDto balanceRequestDto) {
        return balanceService.editBalanceWithAddingAmount(balanceRequestDto);
    }

    @GetMapping("/get/{id}")
    public BalanceResponseDto getBalance(@PathVariable UUID id) {
        return balanceService.getBalance(id);
    }

    @GetMapping("/get/all")
    public List<BalanceResponseDto> getAllBalances(
            @RequestParam(required = false, defaultValue = "0") int page,
            @RequestParam(required = false, defaultValue = "1000000000") int size,
            @RequestParam(required = false) Boolean negative,
            @RequestParam(required = false) BigDecimal value
            ) {
        return balanceService.getAllBalances(page, size, negative, value);
    }

    @GetMapping("/get/all/by/users")
    public List<BalanceResponseDto> getAllBalancesByUsers(@RequestParam UUID[] userIds) {
        return balanceService.getAllBalancesByUsers(Arrays.stream(userIds).toList());
    }

    @GetMapping("/get/short/{id}")
    public BalanceShortResponseDto getBalanceShort(@PathVariable UUID id) {
        return balanceService.getBalanceShort(id);
    }
}
