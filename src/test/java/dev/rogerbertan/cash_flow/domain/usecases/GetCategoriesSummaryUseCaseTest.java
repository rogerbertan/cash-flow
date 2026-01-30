package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cash_flow.domain.usecases.summary.GetCategoriesSummaryUseCase;
import dev.rogerbertan.cash_flow.domain.valueobjects.CategorySummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetCategoriesSummaryUseCaseTest {

    @Mock
    private TransactionGateway transactionGateway;

    @InjectMocks
    private GetCategoriesSummaryUseCase useCase;

    @Test
    void execute_ShouldReturnCategorySummaries_WhenTransactionsExist() {
        // Arrange
        int month = 1;
        int year = 2025;
        CategorySummary summary1 = new CategorySummary("Salary", new BigDecimal("3000.00"), new BigDecimal("0.00"));
        CategorySummary summary2 = new CategorySummary("Food", new BigDecimal("0.00"), new BigDecimal("500.00"));
        CategorySummary summary3 = new CategorySummary("Transport", new BigDecimal("0.00"), new BigDecimal("200.00"));

        List<CategorySummary> summaries = Arrays.asList(summary1, summary2, summary3);
        when(transactionGateway.getCategorySummaries(month, year)).thenReturn(summaries);

        // Act
        List<CategorySummary> result = useCase.execute(month, year);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(3);
        assertThat(result.get(0).categoryName()).isEqualTo("Salary");
        assertThat(result.get(1).categoryName()).isEqualTo("Food");
        assertThat(result.get(2).categoryName()).isEqualTo("Transport");
        verify(transactionGateway, times(1)).getCategorySummaries(month, year);
    }

    @Test
    void execute_ShouldRoundAllSummaryValues_WhenDecimalsPresent() {
        // Arrange
        int month = 6;
        int year = 2024;
        CategorySummary summary1 = new CategorySummary("Salary", new BigDecimal("100.555"), new BigDecimal("0.00"));
        CategorySummary summary2 = new CategorySummary("Food", new BigDecimal("0.00"), new BigDecimal("50.444"));

        List<CategorySummary> summaries = Arrays.asList(summary1, summary2);
        when(transactionGateway.getCategorySummaries(month, year)).thenReturn(summaries);

        // Act
        List<CategorySummary> result = useCase.execute(month, year);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).totalIncome()).isEqualByComparingTo(new BigDecimal("100.56").setScale(2, RoundingMode.HALF_UP));
        assertThat(result.get(0).totalExpense()).isEqualByComparingTo(new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP));
        assertThat(result.get(1).totalIncome()).isEqualByComparingTo(new BigDecimal("0.00").setScale(2, RoundingMode.HALF_UP));
        assertThat(result.get(1).totalExpense()).isEqualByComparingTo(new BigDecimal("50.44").setScale(2, RoundingMode.HALF_UP));
        assertThat(result.get(0).totalIncome().scale()).isEqualTo(2);
        assertThat(result.get(1).totalExpense().scale()).isEqualTo(2);
    }

    @Test
    void execute_ShouldPreserveCategoryNames_WhenMapping() {
        // Arrange
        int month = 3;
        int year = 2024;
        CategorySummary summary1 = new CategorySummary("Salary", new BigDecimal("1000"), new BigDecimal("0"));
        CategorySummary summary2 = new CategorySummary("Groceries", new BigDecimal("0"), new BigDecimal("300"));

        List<CategorySummary> summaries = Arrays.asList(summary1, summary2);
        when(transactionGateway.getCategorySummaries(month, year)).thenReturn(summaries);

        // Act
        List<CategorySummary> result = useCase.execute(month, year);

        // Assert
        assertThat(result).hasSize(2);
        assertThat(result.get(0).categoryName()).isEqualTo("Salary");
        assertThat(result.get(1).categoryName()).isEqualTo("Groceries");
    }

    @Test
    void execute_ShouldReturnEmptyList_WhenNoCategoriesHaveTransactions() {
        // Arrange
        int month = 12;
        int year = 2023;
        when(transactionGateway.getCategorySummaries(month, year)).thenReturn(Collections.emptyList());

        // Act
        List<CategorySummary> result = useCase.execute(month, year);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        verify(transactionGateway, times(1)).getCategorySummaries(month, year);
    }

    @Test
    void execute_ShouldMapAllFieldsCorrectly_WhenTransforming() {
        // Arrange
        int month = 8;
        int year = 2024;
        CategorySummary inputSummary = new CategorySummary(
                "Food",
                new BigDecimal("100.555"),
                new BigDecimal("50.444")
        );

        List<CategorySummary> summaries = Collections.singletonList(inputSummary);
        when(transactionGateway.getCategorySummaries(month, year)).thenReturn(summaries);

        // Act
        List<CategorySummary> result = useCase.execute(month, year);

        // Assert
        assertThat(result).hasSize(1);
        CategorySummary mappedSummary = result.get(0);
        assertThat(mappedSummary.categoryName()).isEqualTo("Food");
        assertThat(mappedSummary.totalIncome()).isEqualByComparingTo(new BigDecimal("100.56"));
        assertThat(mappedSummary.totalExpense()).isEqualByComparingTo(new BigDecimal("50.44"));
        assertThat(mappedSummary.totalIncome().scale()).isEqualTo(2);
        assertThat(mappedSummary.totalExpense().scale()).isEqualTo(2);
    }
}
