package dev.rogerbertan.cashflow.infra.dto;

import java.math.BigDecimal;

public record MonthlySummaryResponse(
        BigDecimal totalIncome, BigDecimal totalExpense, BigDecimal netBalance) {}
