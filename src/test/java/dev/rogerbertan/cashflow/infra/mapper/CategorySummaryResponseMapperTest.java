package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary;
import dev.rogerbertan.cashflow.infra.dto.CategoriesSummaryResponse;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;

class CategorySummaryResponseMapperTest {

    private final CategorySummaryResponseMapper mapper = new CategorySummaryResponseMapper();

    @Test
    void toDTO_ShouldMapCategoryNameIncomeAndExpense_WhenCalled() {
        CategorySummary summary = TestDataFactory.createIncomeCategorySummary();

        CategoriesSummaryResponse result = mapper.toDTO(summary);

        assertThat(result.category()).isEqualTo(summary.categoryName());
        assertThat(result.totalIncome()).isEqualTo(summary.totalIncome());
        assertThat(result.totalExpense()).isEqualTo(summary.totalExpense());
    }

    @Test
    void toListDTO_ShouldMapAllSummaries_WhenListIsProvided() {
        CategorySummary income = TestDataFactory.createIncomeCategorySummary();
        CategorySummary expense = TestDataFactory.createExpenseCategorySummary();

        List<CategoriesSummaryResponse> result = mapper.toListDTO(List.of(income, expense));

        assertThat(result).hasSize(2);
        assertThat(result.get(0).category()).isEqualTo("Salary");
        assertThat(result.get(1).category()).isEqualTo("Food");
    }

    @Test
    void toListDTO_ShouldReturnEmptyList_WhenInputListIsEmpty() {
        List<CategoriesSummaryResponse> result = mapper.toListDTO(Collections.emptyList());

        assertThat(result).isEmpty();
    }
}
