package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.valueobjects.MonthlySummary;
import dev.rogerbertan.cashflow.infra.dto.MonthlySummaryResponse;
import org.junit.jupiter.api.Test;

class MonthlySummaryResponseMapperTest {

    private final MonthlySummaryResponseMapper mapper = new MonthlySummaryResponseMapper();

    @Test
    void toDTO_ShouldMapTotalIncomeExpenseAndNetBalance_WhenCalled() {
        MonthlySummary summary = TestDataFactory.createDefaultMonthlySummary();

        MonthlySummaryResponse result = mapper.toDTO(summary);

        assertThat(result.totalIncome()).isEqualTo(summary.totalIncome());
        assertThat(result.totalExpense()).isEqualTo(summary.totalExpense());
        assertThat(result.netBalance()).isEqualTo(summary.netBalance());
    }
}
