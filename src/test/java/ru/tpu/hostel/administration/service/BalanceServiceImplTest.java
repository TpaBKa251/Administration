package ru.tpu.hostel.administration.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.dto.request.BalanceRequestDto;
import ru.tpu.hostel.administration.dto.response.BalanceResponseDto;
import ru.tpu.hostel.administration.dto.response.BalanceShortResponseDto;
import ru.tpu.hostel.administration.entity.Balance;
import ru.tpu.hostel.administration.repository.BalanceRepository;
import ru.tpu.hostel.administration.service.impl.BalanceServiceImpl;
import ru.tpu.hostel.internal.exception.ServiceException;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class BalanceServiceImplTest {

    @Mock
    private BalanceRepository balanceRepository;

    @InjectMocks
    private BalanceServiceImpl balanceService;

    @Test
    void addBalanceWithSuccess() {
        when(balanceRepository.save(any(Balance.class))).thenReturn(TestData.defaultBalance());

        BalanceResponseDto result = balanceService.addBalance(TestData.defaultBalanceRequestDto());

        assertThat(result.user()).isEqualTo(TestData.USER_ID);
        assertThat(result.balance()).isEqualTo(TestData.BALANCE_VALUE);
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    void editBalanceWhenNotExistsCreatesNew() {
        when(balanceRepository.findByIdOptimistic(TestData.USER_ID)).thenReturn(Optional.empty());

        BalanceResponseDto result = balanceService.editBalance(TestData.defaultBalanceRequestDto());

        assertThat(result.user()).isEqualTo(TestData.USER_ID);
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    void editBalanceWithSuccess() {
        Balance balance = TestData.defaultBalance();
        when(balanceRepository.findByIdOptimistic(TestData.USER_ID)).thenReturn(Optional.of(balance));
        BalanceRequestDto dto = TestData.balanceRequestDto(TestData.USER_ID, TestData.NEGATIVE_BALANCE_VALUE);

        BalanceResponseDto result = balanceService.editBalance(dto);

        assertThat(result.balance()).isEqualTo(TestData.NEGATIVE_BALANCE_VALUE);
        verify(balanceRepository).save(balance);
    }

    @Test
    void editBalanceWhenOptimisticLock() {
        Balance balance = TestData.defaultBalance();
        when(balanceRepository.findByIdOptimistic(TestData.USER_ID)).thenReturn(Optional.of(balance));
        doThrow(new ObjectOptimisticLockingFailureException(Balance.class, TestData.USER_ID))
                .when(balanceRepository).save(balance);

        assertThatThrownBy(() -> balanceService.editBalance(TestData.defaultBalanceRequestDto()))
                .isInstanceOf(ServiceException.Conflict.class);
    }

    @Test
    void editBalanceWithAddingAmountWhenNotExistsCreatesNew() {
        when(balanceRepository.findByIdOptimistic(TestData.USER_ID)).thenReturn(Optional.empty());

        BalanceResponseDto result = balanceService.editBalanceWithAddingAmount(TestData.defaultBalanceRequestDto());

        assertThat(result.user()).isEqualTo(TestData.USER_ID);
        verify(balanceRepository).save(any(Balance.class));
    }

    @Test
    void editBalanceWithAddingAmountWithSuccess() {
        Balance balance = TestData.defaultBalance();
        when(balanceRepository.findByIdOptimistic(TestData.USER_ID)).thenReturn(Optional.of(balance));
        BalanceRequestDto dto = TestData.balanceRequestDto(TestData.USER_ID, TestData.ADD_AMOUNT);

        BalanceResponseDto result = balanceService.editBalanceWithAddingAmount(dto);

        assertThat(result.balance()).isEqualTo(TestData.ADD_AMOUNT.add(TestData.BALANCE_VALUE));
        verify(balanceRepository).flush();
    }

    @Test
    void editBalanceWithAddingAmountWhenOptimisticLock() {
        Balance balance = TestData.defaultBalance();
        when(balanceRepository.findByIdOptimistic(TestData.USER_ID)).thenReturn(Optional.of(balance));
        doThrow(new ObjectOptimisticLockingFailureException(Balance.class, TestData.USER_ID))
                .when(balanceRepository).flush();

        assertThatThrownBy(() -> balanceService.editBalanceWithAddingAmount(
                TestData.balanceRequestDto(TestData.USER_ID, TestData.ADD_AMOUNT)))
                .isInstanceOf(ServiceException.Conflict.class);
    }

    @Test
    void getBalanceWithSuccess() {
        when(balanceRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(TestData.defaultBalance()));

        BalanceResponseDto result = balanceService.getBalance(TestData.USER_ID);

        assertThat(result.user()).isEqualTo(TestData.USER_ID);
    }

    @Test
    void getBalanceWhenNotFound() {
        when(balanceRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.getBalance(TestData.USER_ID))
                .isInstanceOf(ServiceException.BadRequest.class);
    }

    @Test
    void getBalanceShortWithSuccess() {
        when(balanceRepository.findById(TestData.USER_ID)).thenReturn(Optional.of(TestData.defaultBalance()));

        BalanceShortResponseDto result = balanceService.getBalanceShort(TestData.USER_ID);

        assertThat(result.balance()).isEqualTo(TestData.BALANCE_VALUE);
    }

    @Test
    void getBalanceShortWhenNotFound() {
        when(balanceRepository.findById(TestData.USER_ID)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> balanceService.getBalanceShort(TestData.USER_ID))
                .isInstanceOf(ServiceException.BadRequest.class);
    }

    @Test
    void getAllBalancesWhenNegative() {
        Page<Balance> page = new PageImpl<>(List.of(TestData.defaultBalance()));
        when(balanceRepository.findAllByBalanceLessThanEqual(any(BigDecimal.class), any(Pageable.class)))
                .thenReturn(page);

        List<BalanceResponseDto> result = balanceService.getAllBalances(
                TestData.PAGE, TestData.SIZE, true, TestData.THRESHOLD_VALUE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllBalancesWhenNotNegative() {
        Page<Balance> page = new PageImpl<>(List.of(TestData.defaultBalance()));
        when(balanceRepository.findAllByBalanceGreaterThan(any(BigDecimal.class), any(Pageable.class)))
                .thenReturn(page);

        List<BalanceResponseDto> result = balanceService.getAllBalances(
                TestData.PAGE, TestData.SIZE, false, TestData.THRESHOLD_VALUE);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllBalancesWhenNoFilter() {
        Page<Balance> page = new PageImpl<>(List.of(TestData.defaultBalance()));
        when(balanceRepository.findAll(any(Pageable.class))).thenReturn(page);

        List<BalanceResponseDto> result = balanceService.getAllBalances(
                TestData.PAGE, TestData.SIZE, null, null);

        assertThat(result).hasSize(1);
    }

    @Test
    void getAllBalancesByUsersWithSuccess() {
        when(balanceRepository.findByUserInOrderByUser(anyList()))
                .thenReturn(List.of(TestData.defaultBalance()));

        List<BalanceResponseDto> result = balanceService.getAllBalancesByUsers(List.of(TestData.USER_ID));

        assertThat(result).hasSize(1);
    }
}
