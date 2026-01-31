package dev.rogerbertan.cashflow.domain.valueobjects;

import dev.rogerbertan.cashflow.domain.entities.Category;

public record CategorySuggestion(Category category, String confidence, String rawAiResponse) {}
