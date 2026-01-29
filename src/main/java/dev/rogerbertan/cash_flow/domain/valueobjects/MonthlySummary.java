package dev.rogerbertan.cash_flow.domain.valueobjects;

import java.math.BigDecimal;

public record MonthlySummary(
        BigDecimal totalIncome,
        BigDecimal totalExpense,
        BigDecimal netBalance
) {
}