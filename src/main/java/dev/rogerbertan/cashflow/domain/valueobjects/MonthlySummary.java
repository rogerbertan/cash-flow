package dev.rogerbertan.cashflow.domain.valueobjects;

import java.math.BigDecimal;

public record MonthlySummary(
        BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netBalance) {}
