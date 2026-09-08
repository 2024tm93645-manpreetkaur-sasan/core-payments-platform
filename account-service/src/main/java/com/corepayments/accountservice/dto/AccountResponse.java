package com.corepayments.accountservice.dto;

import com.corepayments.accountservice.model.Account;
import com.corepayments.accountservice.model.AccountType;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record AccountResponse(
        Long id,
        String accountNumber,
        String customerName,
        AccountType accountType,
        BigDecimal balance,
        LocalDateTime createdAt
) {
    public static AccountResponse from(Account account) {
        return new AccountResponse(
                account.getId(),
                account.getAccountNumber(),
                account.getCustomerName(),
                account.getAccountType(),
                account.getBalance(),
                account.getCreatedAt()
        );
    }
}