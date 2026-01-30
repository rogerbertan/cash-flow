package dev.rogerbertan.cash_flow.infra.dto;

import java.util.List;

public record SpendingInsightsResponse(
        List<String> insights,
        String period,
        String summary,
        String generatedAt
) {
}