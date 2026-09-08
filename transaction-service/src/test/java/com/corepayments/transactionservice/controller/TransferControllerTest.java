package com.corepayments.transactionservice.controller;

import com.corepayments.transactionservice.dto.TransferRequest;
import com.corepayments.transactionservice.dto.TransferResponse;
import com.corepayments.transactionservice.exception.TransferFailedException;
import com.corepayments.transactionservice.service.TransferService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(TransferController.class)
class TransferControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private TransferService transferService;

    @Test
    void transfer_returnsOkWithCompletedStatus() throws Exception {
        when(transferService.transfer(any(TransferRequest.class)))
                .thenReturn(new TransferResponse(1L, 2L, BigDecimal.valueOf(100), "COMPLETED", LocalDateTime.now()));

        String body = """
                {"fromAccountId": 1, "toAccountId": 2, "amount": 100}
                """;

        mockMvc.perform(post("/transfers").contentType("application/json").content(body))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("COMPLETED"));
    }

    @Test
    void transfer_returnsUnprocessableEntityWhenServiceThrows() throws Exception {
        when(transferService.transfer(any(TransferRequest.class)))
                .thenThrow(new TransferFailedException("Debit failed for account 1"));

        String body = """
                {"fromAccountId": 1, "toAccountId": 2, "amount": 100}
                """;

        mockMvc.perform(post("/transfers").contentType("application/json").content(body))
                .andExpect(status().isUnprocessableEntity())
                .andExpect(jsonPath("$.error").value("Debit failed for account 1"));
    }
}