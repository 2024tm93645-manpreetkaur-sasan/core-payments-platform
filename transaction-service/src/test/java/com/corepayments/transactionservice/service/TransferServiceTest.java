package com.corepayments.transactionservice.service;

import com.corepayments.transactionservice.client.AccountServiceClient;
import com.corepayments.transactionservice.dto.AmountRequest;
import com.corepayments.transactionservice.dto.TransferRequest;
import com.corepayments.transactionservice.dto.TransferResponse;
import com.corepayments.transactionservice.exception.TransferFailedException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.web.client.RestClientException;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.*;

class TransferServiceTest {

    private AccountServiceClient accountServiceClient;
    private TransferService transferService;

    @BeforeEach
    void setUp() {
        accountServiceClient = Mockito.mock(AccountServiceClient.class);
        transferService = new TransferService(accountServiceClient);
    }

    @Test
    void transfer_succeedsWhenBothDebitAndCreditSucceed() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(100));

        TransferResponse response = transferService.transfer(request);

        assertEquals("COMPLETED", response.status());
        Mockito.verify(accountServiceClient).debit(1L, new AmountRequest(BigDecimal.valueOf(100)));
        Mockito.verify(accountServiceClient).credit(2L, new AmountRequest(BigDecimal.valueOf(100)));
    }

    @Test
    void transfer_throwsWhenDebitFails_andNeverAttemptsCredit() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(100));
        Mockito.when(accountServiceClient.debit(Mockito.eq(1L), Mockito.any()))
                .thenThrow(new RestClientException("insufficient funds"));

        assertThrows(TransferFailedException.class, () -> transferService.transfer(request));

        Mockito.verify(accountServiceClient, Mockito.never()).credit(Mockito.anyLong(), Mockito.any());
    }

    @Test
    void transfer_attemptsCompensatingCreditWhenDestinationCreditFails() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(100));
        Mockito.when(accountServiceClient.credit(Mockito.eq(2L), Mockito.any()))
                .thenThrow(new RestClientException("destination account not found"));

        assertThrows(TransferFailedException.class, () -> transferService.transfer(request));

        // the compensating refund back to the source account
        Mockito.verify(accountServiceClient).credit(1L, new AmountRequest(BigDecimal.valueOf(100)));
    }

    @Test
    void transfer_reportsCriticalFailureWhenCompensationAlsoFails() {
        TransferRequest request = new TransferRequest(1L, 2L, BigDecimal.valueOf(100));
        Mockito.when(accountServiceClient.credit(Mockito.eq(2L), Mockito.any()))
                .thenThrow(new RestClientException("destination account not found"));
        Mockito.when(accountServiceClient.credit(Mockito.eq(1L), Mockito.any()))
                .thenThrow(new RestClientException("account service unreachable"));

        TransferFailedException ex = assertThrows(TransferFailedException.class,
                () -> transferService.transfer(request));

        assertTrue(ex.getMessage().contains("CRITICAL"));
    }
}