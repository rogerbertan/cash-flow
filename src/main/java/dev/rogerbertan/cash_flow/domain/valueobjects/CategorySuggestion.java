package dev.rogerbertan.cash_flow.domain.valueobjects;

import dev.rogerbertan.cash_flow.domain.entities.Category;

public record CategorySuggestion(
        Category category,
        String confidence,
        String rawAiResponse
) {
}