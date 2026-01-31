package dev.rogerbertan.cashflow.infra.dto;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record TransactionUpdateRequest(
        Long id,
        Type type,
        BigDecimal amount,
        String description,
        Category category,
        LocalDate transactionDate,
        LocalDateTime createdAt) {}
