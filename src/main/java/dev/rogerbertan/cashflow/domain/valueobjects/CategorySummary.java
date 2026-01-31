package dev.rogerbertan.cashflow.domain.valueobjects;

import java.math.BigDecimal;

public record CategorySummary(
        String categoryName, BigDecimal totalIncome, BigDecimal totalExpense) {}
