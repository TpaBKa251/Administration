package ru.tpu.hostel.administration.mapper;

import org.junit.jupiter.api.Test;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.dto.request.BalanceRequestDto;
import ru.tpu.hostel.administration.dto.response.BalanceResponseDto;
import ru.tpu.hostel.administration.dto.response.BalanceShortResponseDto;
import ru.tpu.hostel.administration.entity.Balance;

import static org.assertj.core.api.Assertions.assertThat;

class BalanceMapperTest {

    @Test
    void mapBalanceToBalanceResponseDtoWithSuccess() {
        Balance balance = TestData.defaultBalance();

        BalanceResponseDto result = BalanceMapper.mapBalanceToBalanceResponseDto(balance);

        assertThat(result.user()).isEqualTo(TestData.USER_ID);
        assertThat(result.balance()).isEqualTo(TestData.BALANCE_VALUE);
    }

    @Test
    void mapBalanceToBalanceShortResponseDtoWithSuccess() {
        Balance balance = TestData.defaultBalance();

        BalanceShortResponseDto result = BalanceMapper.mapBalanceToBalanceShortResponseDto(balance);

        assertThat(result.balance()).isEqualTo(TestData.BALANCE_VALUE);
    }

    @Test
    void mapBalanceRequestDtoToBalanceWithSuccess() {
        BalanceRequestDto dto = TestData.defaultBalanceRequestDto();

        Balance result = BalanceMapper.mapBalanceRequestDtoToBalance(dto);

        assertThat(result.getUser()).isEqualTo(TestData.USER_ID);
        assertThat(result.getBalance()).isEqualTo(TestData.BALANCE_VALUE);
    }
}
