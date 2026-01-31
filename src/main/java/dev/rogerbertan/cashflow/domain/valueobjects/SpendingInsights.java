package dev.rogerbertan.cashflow.domain.valueobjects;

import java.util.List;

public record SpendingInsights(
        List<String> insights, String period, String summary, String rawAiResponse) {}
