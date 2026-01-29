package dev.rogerbertan.cash_flow.domain.entities;

import dev.rogerbertan.cash_flow.domain.enums.Type;

import java.time.LocalDateTime;

public record Category(
        Long id,
        String name,
        Type type,
        LocalDateTime createdAt
) {
}
