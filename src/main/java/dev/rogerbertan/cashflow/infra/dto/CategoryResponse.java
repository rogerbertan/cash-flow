package dev.rogerbertan.cashflow.infra.dto;

import dev.rogerbertan.cashflow.domain.enums.Type;

public record CategoryResponse(Long id, String name, Type type) {}
