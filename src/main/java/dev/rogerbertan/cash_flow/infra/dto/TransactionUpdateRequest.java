package dev.rogerbertan.cash_flow.infra.dto;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.enums.Type;

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
        LocalDateTime createdAt
) {}
