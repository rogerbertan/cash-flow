package dev.rogerbertan.cashflow.infra.dto;

public record CategorySuggestionResponse(
        CategoryResponse suggestedCategory, String confidence, String message) {}
