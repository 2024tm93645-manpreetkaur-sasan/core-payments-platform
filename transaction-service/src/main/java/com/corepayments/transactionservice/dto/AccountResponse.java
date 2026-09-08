package com.corepayments.transactionservice.dto;

import java.math.BigDecimal;

public record AccountResponse(
        Long id,
        String accountNumber,
        String customerName,
        String accountType,
        BigDecimal balance,
        String createdAt
) {}