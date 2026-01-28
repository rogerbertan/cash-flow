package dev.rogerbertan.budget_planner_clean_arch.infra.mapper;

import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.Balance;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.BalanceResponse;
import org.springframework.stereotype.Component;

@Component
public class BalanceResponseMapper {

    public BalanceResponse toDTO(Balance balance) {
        return new BalanceResponse(balance.balance());
    }
}