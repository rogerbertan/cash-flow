package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.valueobjects.MonthlySummary;
import dev.rogerbertan.cashflow.infra.dto.MonthlySummaryResponse;
import org.springframework.stereotype.Component;

@Component
public class MonthlySummaryResponseMapper {

    public MonthlySummaryResponse toDTO(MonthlySummary summary) {
        return new MonthlySummaryResponse(
                summary.totalIncome(), summary.totalExpense(), summary.netBalance());
    }
}
