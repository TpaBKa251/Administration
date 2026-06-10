package ru.tpu.hostel.administration.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import ru.tpu.hostel.administration.TestData;
import ru.tpu.hostel.administration.service.BalanceService;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(BalanceController.class)
@AutoConfigureMockMvc(addFilters = false)
class BalanceControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private BalanceService balanceService;

    @Test
    void addBalanceWithSuccess() throws Exception {
        when(balanceService.addBalance(any())).thenReturn(TestData.balanceResponseDto());

        mockMvc.perform(post("/balance")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestData.defaultBalanceRequestDto())))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value(TestData.USER_ID.toString()));
    }

    @Test
    void editBalanceWithSuccess() throws Exception {
        when(balanceService.editBalance(any())).thenReturn(TestData.balanceResponseDto());

        mockMvc.perform(patch("/balance/edit")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestData.defaultBalanceRequestDto())))
                .andExpect(status().isOk());
    }

    @Test
    void editAddBalanceWithSuccess() throws Exception {
        when(balanceService.editBalanceWithAddingAmount(any())).thenReturn(TestData.balanceResponseDto());

        mockMvc.perform(patch("/balance/edit/adding")
                        .contentType("application/json")
                        .content(objectMapper.writeValueAsString(TestData.defaultBalanceRequestDto())))
                .andExpect(status().isOk());
    }

    @Test
    void getBalanceWithSuccess() throws Exception {
        when(balanceService.getBalance(TestData.USER_ID)).thenReturn(TestData.balanceResponseDto());

        mockMvc.perform(get("/balance/get/{id}", TestData.USER_ID))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.user").value(TestData.USER_ID.toString()));
    }

    @Test
    void getAllBalancesWithSuccess() throws Exception {
        when(balanceService.getAllBalances(anyInt(), anyInt(), any(), any()))
                .thenReturn(List.of(TestData.balanceResponseDto()));

        mockMvc.perform(get("/balance/get/all"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    @Test
    void getBalanceShortWithSuccess() throws Exception {
        when(balanceService.getBalanceShort(TestData.USER_ID)).thenReturn(TestData.balanceShortResponseDto());

        mockMvc.perform(get("/balance/get/short/{id}", TestData.USER_ID))
                .andExpect(status().isOk());
    }
}
