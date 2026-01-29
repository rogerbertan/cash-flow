package dev.rogerbertan.cash_flow.infra.dto;

import java.math.BigDecimal;

public record BalanceResponse(
        BigDecimal balance
) {
}
