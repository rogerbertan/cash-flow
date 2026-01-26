package dev.rogerbertan.budget_planner_clean_arch.infra.dto;

import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;

import java.time.LocalDateTime;

public record CategoryUpdateRequest(
    Long id,
    String name,
    Type type,
    LocalDateTime createdAt
) {}
