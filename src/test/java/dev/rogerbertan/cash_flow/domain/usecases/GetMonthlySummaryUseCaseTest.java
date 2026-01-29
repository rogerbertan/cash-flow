package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cash_flow.domain.valueobjects.MonthlySummary;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.math.RoundingMode;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GetMonthlySummaryUseCaseTest {

    @Mock
    private TransactionGateway transactionGateway;

    @InjectMocks
    private GetMonthlySummaryUseCase useCase;

    @Test
    void execute_ShouldCalculateMonthlySummary_WhenTransactionsExist() {
        // Arrange
        int month = 6;
        int year = 2024;
        BigDecimal monthlyIncome = new BigDecimal("2000.00");
        BigDecimal monthlyExpense = new BigDecimal("1200.00");

        when(transactionGateway.getMonthlyIncome(month, year)).thenReturn(monthlyIncome);
        when(transactionGateway.getMonthlyExpense(month, year)).thenReturn(monthlyExpense);

        // Act
        MonthlySummary result = useCase.execute(month, year);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.totalIncome()).isEqualByComparingTo(new BigDecimal("2000.00"));
        assertThat(result.totalExpense()).isEqualByComparingTo(new BigDecimal("1200.00"));
        assertThat(result.netBalance()).isEqualByComparingTo(new BigDecimal("800.00"));
        assertThat(result.totalIncome().scale()).isEqualTo(2);
        assertThat(result.totalExpense().scale()).isEqualTo(2);
        assertThat(result.netBalance().scale()).isEqualTo(2);
    }

    @Test
    void execute_ShouldPassCorrectParameters_WhenCalled() {
        // Arrange
        int month = 3;
        int year = 2025;
        when(transactionGateway.getMonthlyIncome(month, year)).thenReturn(BigDecimal.ZERO);
        when(transactionGateway.getMonthlyExpense(month, year)).thenReturn(BigDecimal.ZERO);

        ArgumentCaptor<Integer> monthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);

        // Act
        useCase.execute(month, year);

        // Assert
        verify(transactionGateway, times(1)).getMonthlyIncome(monthCaptor.capture(), yearCaptor.capture());
        verify(transactionGateway, times(1)).getMonthlyExpense(monthCaptor.capture(), yearCaptor.capture());

        assertThat(monthCaptor.getAllValues()).containsExactly(3, 3);
        assertThat(yearCaptor.getAllValues()).containsExactly(2025, 2025);
    }

    @Test
    void execute_ShouldRoundAllValues_WhenDecimalsPresent() {
        // Arrange
        int month = 12;
        int year = 2024;
        BigDecimal monthlyIncome = new BigDecimal("1500.555");
        BigDecimal monthlyExpense = new BigDecimal("800.444");

        when(transactionGateway.getMonthlyIncome(month, year)).thenReturn(monthlyIncome);
        when(transactionGateway.getMonthlyExpense(month, year)).thenReturn(monthlyExpense);

        // Act
        MonthlySummary result = useCase.execute(month, year);

        // Assert
        assertThat(result.totalIncome()).isEqualByComparingTo(new BigDecimal("1500.56").setScale(2, RoundingMode.HALF_UP));
        assertThat(result.totalExpense()).isEqualByComparingTo(new BigDecimal("800.44").setScale(2, RoundingMode.HALF_UP));
        assertThat(result.netBalance()).isEqualByComparingTo(new BigDecimal("700.12").setScale(2, RoundingMode.HALF_UP));
        assertThat(result.totalIncome().scale()).isEqualTo(2);
        assertThat(result.totalExpense().scale()).isEqualTo(2);
        assertThat(result.netBalance().scale()).isEqualTo(2);
    }

    @Test
    void execute_ShouldHandleZeroValues_WhenNoTransactionsInMonth() {
        // Arrange
        int month = 1;
        int year = 2024;
        when(transactionGateway.getMonthlyIncome(month, year)).thenReturn(BigDecimal.ZERO);
        when(transactionGateway.getMonthlyExpense(month, year)).thenReturn(BigDecimal.ZERO);

        // Act
        MonthlySummary result = useCase.execute(month, year);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.totalIncome()).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(result.totalExpense()).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(result.netBalance()).isEqualByComparingTo(new BigDecimal("0.00"));
    }
}
