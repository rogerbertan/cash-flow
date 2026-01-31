package dev.rogerbertan.cashflow.infra.dto;

import dev.rogerbertan.cashflow.domain.enums.Type;

public record CategorySuggestionRequest(String description, Type type) {}
