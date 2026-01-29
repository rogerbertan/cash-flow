package dev.rogerbertan.cash_flow.infra.mapper;

import dev.rogerbertan.cash_flow.domain.valueobjects.Balance;
import dev.rogerbertan.cash_flow.infra.dto.BalanceResponse;
import org.springframework.stereotype.Component;

@Component
public class BalanceResponseMapper {

    public BalanceResponse toDTO(Balance balance) {
        return new BalanceResponse(balance.balance());
    }
}