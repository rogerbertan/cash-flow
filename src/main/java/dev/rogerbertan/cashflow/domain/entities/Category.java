package dev.rogerbertan.cashflow.domain.entities;

import dev.rogerbertan.cashflow.domain.enums.Type;
import java.time.LocalDateTime;

public record Category(Long id, String name, Type type, LocalDateTime createdAt) {}
