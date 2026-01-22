package dev.rogerbertan.budget_planner_clean_arch.domain.entities;

import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;

import java.time.LocalDateTime;

public record Category(
        Long id,
        String name,
        Type type,
        LocalDateTime createdAt
) {
}
