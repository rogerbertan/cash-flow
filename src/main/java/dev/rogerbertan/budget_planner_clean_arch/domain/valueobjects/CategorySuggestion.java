package dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;

public record CategorySuggestion(
        Category category,
        String confidence,
        String rawAiResponse
) {
}