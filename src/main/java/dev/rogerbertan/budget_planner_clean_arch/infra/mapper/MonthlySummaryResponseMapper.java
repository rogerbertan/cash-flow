package dev.rogerbertan.budget_planner_clean_arch.infra.mapper;

import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.MonthlySummary;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.MonthlySummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class MonthlySummaryResponseMapper {

    public MonthlySummaryResponse toDTO(MonthlySummary summary) {
        return new MonthlySummaryResponse(
                summary.totalIncome(),
                summary.totalExpense(),
                summary.netBalance()
        );
    }
}