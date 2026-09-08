package com.corepayments.transactionservice.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record TransferResponse(
        Long fromAccountId,
        Long toAccountId,
        BigDecimal amount,
        String status,
        LocalDateTime completedAt
) {}