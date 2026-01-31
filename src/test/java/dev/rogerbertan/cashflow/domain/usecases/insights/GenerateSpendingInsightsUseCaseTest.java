package dev.rogerbertan.cashflow.domain.usecases.insights;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.AIInsightsGateway;
import dev.rogerbertan.cashflow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cashflow.domain.valueobjects.TransactionAnalysisData;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GenerateSpendingInsightsUseCaseTest {

    @Mock private AIInsightsGateway aiInsightsGateway;

    @Mock private TransactionGateway transactionGateway;

    @InjectMocks private GenerateSpendingInsightsUseCase useCase;

    private List<Transaction> sampleTransactions;
    private Category expenseCategory;

    @BeforeEach
    void setUp() {
        expenseCategory = new Category(1L, "Groceries", Type.EXPENSE, LocalDateTime.now());

        sampleTransactions =
                List.of(
                        new Transaction(
                                1L,
                                Type.EXPENSE,
                                new BigDecimal("50.00"),
                                "Shopping",
                                expenseCategory,
                                LocalDate.now(),
                                LocalDateTime.now()),
                        new Transaction(
                                2L,
                                Type.EXPENSE,
                                new BigDecimal("30.00"),
                                "Coffee",
                                expenseCategory,
                                LocalDate.now(),
                                LocalDateTime.now()),
                        new Transaction(
                                3L,
                                Type.EXPENSE,
                                new BigDecimal("100.00"),
                                "Restaurant",
                                expenseCategory,
                                LocalDate.now(),
                                LocalDateTime.now()));
    }

    @Test
    void execute_ShouldReturnInsights_WhenValidPeriodProvided() {
        when(transactionGateway.findTransactionsByDateRange(any(), any()))
                .thenReturn(sampleTransactions);
        when(transactionGateway.getExpensesByDayOfWeek(any(), any())).thenReturn(new HashMap<>());
        when(transactionGateway.getTransactionCountByCategory(any(), any()))
                .thenReturn(new HashMap<>());
        when(transactionGateway.getAverageAmountByCategory(any(), any()))
                .thenReturn(new HashMap<>());

        SpendingInsights expectedInsights =
                new SpendingInsights(
                        List.of("Your spending increased 20%"),
                        "monthly",
                        "Good spending habits",
                        "raw_response");
        when(aiInsightsGateway.generateInsights(any())).thenReturn(expectedInsights);

        SpendingInsights result = useCase.execute("monthly");

        assertNotNull(result);
        assertEquals("monthly", result.period());
        verify(aiInsightsGateway).generateInsights(any(TransactionAnalysisData.class));
    }

    @Test
    void execute_ShouldReturnInsufficientData_WhenLessThanThreeTransactions() {
        List<Transaction> fewTransactions =
                List.of(
                        new Transaction(
                                1L,
                                Type.EXPENSE,
                                new BigDecimal("50.00"),
                                "Shopping",
                                expenseCategory,
                                LocalDate.now(),
                                LocalDateTime.now()));

        when(transactionGateway.findTransactionsByDateRange(any(), any()))
                .thenReturn(fewTransactions);

        SpendingInsights result = useCase.execute("monthly");

        assertNotNull(result);
        assertEquals("monthly", result.period());
        assertTrue(result.insights().get(0).contains("Insufficient data"));
        verify(aiInsightsGateway, never()).generateInsights(any());
    }

    @Test
    void execute_ShouldReturnNoData_WhenNoTransactions() {
        when(transactionGateway.findTransactionsByDateRange(any(), any())).thenReturn(List.of());

        SpendingInsights result = useCase.execute("monthly");

        assertNotNull(result);
        assertEquals("monthly", result.period());
        assertTrue(result.insights().get(0).contains("No transactions found"));
        verify(aiInsightsGateway, never()).generateInsights(any());
    }

    @Test
    void execute_ShouldPassCorrectDataToGateway() {
        when(transactionGateway.findTransactionsByDateRange(any(), any()))
                .thenReturn(sampleTransactions);
        when(transactionGateway.getExpensesByDayOfWeek(any(), any())).thenReturn(new HashMap<>());
        when(transactionGateway.getTransactionCountByCategory(any(), any()))
                .thenReturn(new HashMap<>());
        when(transactionGateway.getAverageAmountByCategory(any(), any()))
                .thenReturn(new HashMap<>());

        SpendingInsights expectedInsights =
                new SpendingInsights(List.of("Test insight"), "weekly", "Summary", "raw");
        when(aiInsightsGateway.generateInsights(any())).thenReturn(expectedInsights);

        useCase.execute("weekly");

        ArgumentCaptor<TransactionAnalysisData> captor =
                ArgumentCaptor.forClass(TransactionAnalysisData.class);
        verify(aiInsightsGateway).generateInsights(captor.capture());

        TransactionAnalysisData capturedData = captor.getValue();
        assertEquals("weekly", capturedData.period());
        assertNotNull(capturedData.startDate());
        assertNotNull(capturedData.endDate());
    }
}
