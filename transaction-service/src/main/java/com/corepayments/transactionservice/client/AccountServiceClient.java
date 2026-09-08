package com.corepayments.transactionservice.client;

import com.corepayments.transactionservice.dto.AccountResponse;
import com.corepayments.transactionservice.dto.AmountRequest;
import org.springframework.web.service.annotation.GetExchange;
import org.springframework.web.service.annotation.PostExchange;

public interface AccountServiceClient {

    @GetExchange("/accounts/{id}")
    AccountResponse getAccount(Long id);

    @PostExchange("/accounts/{id}/debit")
    AccountResponse debit(Long id, @org.springframework.web.bind.annotation.RequestBody AmountRequest amount);

    @PostExchange("/accounts/{id}/credit")
    AccountResponse credit(Long id, @org.springframework.web.bind.annotation.RequestBody AmountRequest amount);
}