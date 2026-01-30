package dev.rogerbertan.cash_flow.infra.dto;

public record SpendingInsightsRequest(String period) {
    public SpendingInsightsRequest {
        if (period == null || period.isBlank()) {
            period = "monthly";
        }
    }
}