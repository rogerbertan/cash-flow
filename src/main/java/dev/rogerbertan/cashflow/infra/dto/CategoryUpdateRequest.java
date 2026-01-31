package dev.rogerbertan.cashflow.infra.dto;

import dev.rogerbertan.cashflow.domain.enums.Type;
import java.time.LocalDateTime;

public record CategoryUpdateRequest(Long id, String name, Type type, LocalDateTime createdAt) {}
