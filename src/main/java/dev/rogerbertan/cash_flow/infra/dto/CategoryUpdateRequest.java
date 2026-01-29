package dev.rogerbertan.cash_flow.infra.dto;

import dev.rogerbertan.cash_flow.domain.enums.Type;

import java.time.LocalDateTime;

public record CategoryUpdateRequest(
    Long id,
    String name,
    Type type,
    LocalDateTime createdAt
) {}
