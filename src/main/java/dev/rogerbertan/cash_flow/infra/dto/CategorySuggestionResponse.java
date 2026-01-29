package dev.rogerbertan.cash_flow.infra.dto;

public record CategorySuggestionResponse(
        CategoryResponse suggestedCategory,
        String confidence,
        String message
) {
}