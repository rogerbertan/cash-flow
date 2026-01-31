package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.valueobjects.Balance;
import dev.rogerbertan.cashflow.infra.dto.BalanceResponse;
import org.springframework.stereotype.Component;

@Component
public class BalanceResponseMapper {

    public BalanceResponse toDTO(Balance balance) {
        return new BalanceResponse(balance.balance());
    }
}
