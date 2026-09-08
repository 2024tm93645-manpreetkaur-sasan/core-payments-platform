package com.corepayments.transactionservice.dto;

import java.math.BigDecimal;

public record AmountRequest(BigDecimal amount) {}