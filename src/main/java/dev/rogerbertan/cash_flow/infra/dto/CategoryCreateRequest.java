package dev.rogerbertan.cash_flow.infra.dto;

import dev.rogerbertan.cash_flow.domain.enums.Type;

public record CategoryCreateRequest(
    String name,
    Type type
) {
}
