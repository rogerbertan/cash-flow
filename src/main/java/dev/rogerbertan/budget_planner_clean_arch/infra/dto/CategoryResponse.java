package dev.rogerbertan.budget_planner_clean_arch.infra.dto;

import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;

public record CategoryResponse(
    Long id,
    String name,
    Type type
) {
}
