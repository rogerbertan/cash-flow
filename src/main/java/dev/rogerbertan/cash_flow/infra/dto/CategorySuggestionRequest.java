package dev.rogerbertan.cash_flow.infra.dto;

import dev.rogerbertan.cash_flow.domain.enums.Type;

public record CategorySuggestionRequest(
        String description,
        Type type
) {
}