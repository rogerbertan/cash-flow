package dev.rogerbertan.cash_flow.infra.mapper;

import dev.rogerbertan.cash_flow.domain.valueobjects.MonthlySummary;
import dev.rogerbertan.cash_flow.infra.dto.MonthlySummaryResponse;
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