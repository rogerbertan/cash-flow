package dev.rogerbertan.cashflow.infra.dto;

import dev.rogerbertan.cashflow.domain.enums.Type;
import java.math.BigDecimal;
import java.time.LocalDate;

public record TransactionCreateRequest(
        Type type,
        BigDecimal amount,
        String description,
        Long categoryId,
        LocalDate transactionDate) {}
