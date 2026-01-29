package dev.rogerbertan.cash_flow.domain.usecases;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.enums.Type;
import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class FindAllTransactionUseCaseTest {

    @Mock
    private TransactionGateway transactionGateway;

    @InjectMocks
    private FindAllTransactionUseCase useCase;

    @Test
    void execute_ShouldReturnPagedTransactions_WhenTransactionsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Category category = TestDataFactory.createIncomeCategory();
        Transaction transaction1 = new Transaction(1L, Type.INCOME, new BigDecimal("1000.00"), "Salary", category, LocalDate.now(), LocalDateTime.now());
        Transaction transaction2 = new Transaction(2L, Type.EXPENSE, new BigDecimal("500.00"), "Food", category, LocalDate.now(), LocalDateTime.now());
        Transaction transaction3 = new Transaction(3L, Type.INCOME, new BigDecimal("2000.00"), "Bonus", category, LocalDate.now(), LocalDateTime.now());
        Transaction transaction4 = new Transaction(4L, Type.EXPENSE, new BigDecimal("300.00"), "Transport", category, LocalDate.now(), LocalDateTime.now());
        Transaction transaction5 = new Transaction(5L, Type.EXPENSE, new BigDecimal("150.00"), "Utilities", category, LocalDate.now(), LocalDateTime.now());

        List<Transaction> transactions = Arrays.asList(transaction1, transaction2, transaction3, transaction4, transaction5);
        Page<Transaction> transactionPage = new PageImpl<>(transactions, pageable, transactions.size());

        when(transactionGateway.findAllTransactions(pageable)).thenReturn(transactionPage);

        // Act
        Page<Transaction> result = useCase.execute(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(5);
        assertThat(result.getTotalElements()).isEqualTo(5);
        assertThat(result.getNumber()).isEqualTo(0);
        verify(transactionGateway, times(1)).findAllTransactions(pageable);
    }

    @Test
    void execute_ShouldPassPageableToGateway_WhenCalled() {
        // Arrange
        Pageable pageable = PageRequest.of(2, 10);
        Page<Transaction> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(transactionGateway.findAllTransactions(pageable)).thenReturn(emptyPage);

        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);

        // Act
        useCase.execute(pageable);

        // Assert
        verify(transactionGateway, times(1)).findAllTransactions(pageableCaptor.capture());
        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(2);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
    }

    @Test
    void execute_ShouldReturnEmptyPage_WhenNoTransactionsExist() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 20);
        Page<Transaction> emptyPage = new PageImpl<>(Collections.emptyList(), pageable, 0);
        when(transactionGateway.findAllTransactions(pageable)).thenReturn(emptyPage);

        // Act
        Page<Transaction> result = useCase.execute(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isEqualTo(0);
        verify(transactionGateway, times(1)).findAllTransactions(pageable);
    }
}