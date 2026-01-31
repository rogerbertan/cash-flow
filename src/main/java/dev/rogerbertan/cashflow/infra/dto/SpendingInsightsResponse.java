package dev.rogerbertan.cashflow.infra.dto;

import java.util.List;

public record SpendingInsightsResponse(
        List<String> insights, String period, String summary, String generatedAt) {}
