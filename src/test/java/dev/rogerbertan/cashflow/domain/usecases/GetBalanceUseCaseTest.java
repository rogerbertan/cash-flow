package dev.rogerbertan.cashflow.domain.usecases;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.domain.usecases.summary.GetBalanceUseCase;
import dev.rogerbertan.cashflow.domain.valueobjects.Balance;
import java.math.BigDecimal;
import java.math.RoundingMode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GetBalanceUseCaseTest {

    @Mock private TransactionGateway transactionGateway;

    @InjectMocks private GetBalanceUseCase useCase;

    @Test
    void execute_ShouldCalculateBalance_WhenTransactionsExist() {
        // Arrange
        BigDecimal totalIncome = new BigDecimal("1500.50");
        BigDecimal totalExpense = new BigDecimal("750.25");
        BigDecimal expectedBalance = new BigDecimal("750.25");

        when(transactionGateway.getTotalIncome()).thenReturn(totalIncome);
        when(transactionGateway.getTotalExpense()).thenReturn(totalExpense);

        // Act
        Balance result = useCase.execute();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.balance()).isEqualByComparingTo(expectedBalance);
        assertThat(result.balance().scale()).isEqualTo(2);
        verify(transactionGateway, times(1)).getTotalIncome();
        verify(transactionGateway, times(1)).getTotalExpense();
    }

    @Test
    void execute_ShouldHandleZeroValues_WhenNoTransactions() {
        // Arrange
        when(transactionGateway.getTotalIncome()).thenReturn(BigDecimal.ZERO);
        when(transactionGateway.getTotalExpense()).thenReturn(BigDecimal.ZERO);

        // Act
        Balance result = useCase.execute();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.balance()).isEqualByComparingTo(new BigDecimal("0.00"));
        assertThat(result.balance().scale()).isEqualTo(2);
    }

    @Test
    void execute_ShouldRoundHalfUp_WhenDecimalsExceedTwoPlaces() {
        // Arrange
        BigDecimal totalIncome = new BigDecimal("100.005");
        BigDecimal totalExpense = new BigDecimal("50.003");
        // 100.005 - 50.003 = 50.002, rounded to 50.00 with HALF_UP

        when(transactionGateway.getTotalIncome()).thenReturn(totalIncome);
        when(transactionGateway.getTotalExpense()).thenReturn(totalExpense);

        // Act
        Balance result = useCase.execute();

        // Assert
        BigDecimal expectedBalance = new BigDecimal("50.00").setScale(2, RoundingMode.HALF_UP);
        assertThat(result.balance()).isEqualByComparingTo(expectedBalance);
        assertThat(result.balance().scale()).isEqualTo(2);
    }

    @Test
    void execute_ShouldHandleNegativeBalance_WhenExpensesExceedIncome() {
        // Arrange
        BigDecimal totalIncome = new BigDecimal("500.00");
        BigDecimal totalExpense = new BigDecimal("800.00");
        BigDecimal expectedBalance = new BigDecimal("-300.00");

        when(transactionGateway.getTotalIncome()).thenReturn(totalIncome);
        when(transactionGateway.getTotalExpense()).thenReturn(totalExpense);

        // Act
        Balance result = useCase.execute();

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.balance()).isEqualByComparingTo(expectedBalance);
        assertThat(result.balance().scale()).isEqualTo(2);
    }
}
