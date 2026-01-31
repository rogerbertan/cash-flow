package dev.rogerbertan.cashflow.domain.entities;

import dev.rogerbertan.cashflow.domain.enums.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

public record Transaction(
        Long id,
        Type type,
        BigDecimal amount,
        String description,
        Category category,
        LocalDate transactionDate,
        LocalDateTime createdAt) {}
