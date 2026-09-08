package com.corepayments.accountservice.dto;

import com.corepayments.accountservice.model.AccountType;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record CreateAccountRequest(
        @NotBlank String customerName,
        @NotNull AccountType accountType,
        @NotNull BigDecimal openingBalance
) {}