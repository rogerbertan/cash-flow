package dev.rogerbertan.cash_flow.domain.valueobjects;

import java.math.BigDecimal;

public record CategorySummary(
        String categoryName,
        BigDecimal totalIncome,
        BigDecimal totalExpense
) {
}