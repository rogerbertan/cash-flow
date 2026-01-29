package dev.rogerbertan.budget_planner_clean_arch.infra.dto;

public record CategorySuggestionResponse(
        CategoryResponse suggestedCategory,
        String confidence,
        String message
) {
}