package dev.rogerbertan.cashflow.infra.presentation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.usecases.*;
import dev.rogerbertan.cashflow.domain.usecases.summary.GetBalanceUseCase;
import dev.rogerbertan.cashflow.domain.usecases.summary.GetCategoriesSummaryUseCase;
import dev.rogerbertan.cashflow.domain.usecases.summary.GetMonthlySummaryUseCase;
import dev.rogerbertan.cashflow.domain.valueobjects.Balance;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary;
import dev.rogerbertan.cashflow.domain.valueobjects.MonthlySummary;
import dev.rogerbertan.cashflow.infra.dto.BalanceResponse;
import dev.rogerbertan.cashflow.infra.dto.CategoriesSummaryResponse;
import dev.rogerbertan.cashflow.infra.dto.MonthlySummaryResponse;
import dev.rogerbertan.cashflow.infra.mapper.BalanceResponseMapper;
import dev.rogerbertan.cashflow.infra.mapper.CategorySummaryResponseMapper;
import dev.rogerbertan.cashflow.infra.mapper.MonthlySummaryResponseMapper;
import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class SummaryControllerTest {

    @Mock private GetBalanceUseCase getBalanceUseCase;

    @Mock private GetMonthlySummaryUseCase getMonthlySummaryUseCase;

    @Mock private GetCategoriesSummaryUseCase getCategoriesSummaryUseCase;

    @Mock private BalanceResponseMapper balanceResponseMapper;

    @Mock private MonthlySummaryResponseMapper monthlySummaryResponseMapper;

    @Mock private CategorySummaryResponseMapper categorySummaryResponseMapper;

    @InjectMocks private SummaryController controller;

    // getBalance tests

    @Test
    void getBalance_ShouldReturnBalance_WhenCalled() {
        // Arrange
        Balance balance = TestDataFactory.createPositiveBalance();
        BalanceResponse response = TestDataFactory.createPositiveBalanceResponse();

        when(getBalanceUseCase.execute()).thenReturn(balance);
        when(balanceResponseMapper.toDTO(balance)).thenReturn(response);

        // Act
        ResponseEntity<BalanceResponse> result = controller.getBalance();

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().balance()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void getBalance_ShouldDelegateToUseCase_WhenCalled() {
        // Arrange
        Balance balance = TestDataFactory.createPositiveBalance();
        BalanceResponse response = TestDataFactory.createPositiveBalanceResponse();

        when(getBalanceUseCase.execute()).thenReturn(balance);
        when(balanceResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.getBalance();

        // Assert
        verify(getBalanceUseCase, times(1)).execute();
        verifyNoMoreInteractions(getBalanceUseCase);
    }

    @Test
    void getBalance_ShouldMapValueObjectToDTO_WhenReturning() {
        // Arrange
        Balance balance = TestDataFactory.createPositiveBalance();
        BalanceResponse response = TestDataFactory.createPositiveBalanceResponse();

        when(getBalanceUseCase.execute()).thenReturn(balance);
        when(balanceResponseMapper.toDTO(balance)).thenReturn(response);

        // Act
        controller.getBalance();

        // Assert
        ArgumentCaptor<Balance> balanceCaptor = ArgumentCaptor.forClass(Balance.class);
        verify(balanceResponseMapper, times(1)).toDTO(balanceCaptor.capture());
        assertThat(balanceCaptor.getValue()).isEqualTo(balance);
    }

    // getMonthlySummary tests

    @Test
    void getMonthlySummary_ShouldReturnSummary_WhenValidMonthAndYearProvided() {
        // Arrange
        int month = 1;
        int year = 2024;
        MonthlySummary monthlySummary = TestDataFactory.createDefaultMonthlySummary();
        MonthlySummaryResponse response = TestDataFactory.createDefaultMonthlySummaryResponse();

        when(getMonthlySummaryUseCase.execute(month, year)).thenReturn(monthlySummary);
        when(monthlySummaryResponseMapper.toDTO(monthlySummary)).thenReturn(response);

        // Act
        ResponseEntity<MonthlySummaryResponse> result = controller.getMonthlySummary(month, year);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().totalIncome()).isEqualByComparingTo(new BigDecimal("5000.00"));
        assertThat(result.getBody().totalExpense()).isEqualByComparingTo(new BigDecimal("3000.00"));
        assertThat(result.getBody().netBalance()).isEqualByComparingTo(new BigDecimal("2000.00"));
    }

    @Test
    void getMonthlySummary_ShouldPassCorrectParams_WhenCalled() {
        // Arrange
        int month = 5;
        int year = 2025;
        MonthlySummary monthlySummary = TestDataFactory.createDefaultMonthlySummary();
        MonthlySummaryResponse response = TestDataFactory.createDefaultMonthlySummaryResponse();

        when(getMonthlySummaryUseCase.execute(month, year)).thenReturn(monthlySummary);
        when(monthlySummaryResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.getMonthlySummary(month, year);

        // Assert
        ArgumentCaptor<Integer> monthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(getMonthlySummaryUseCase, times(1))
                .execute(monthCaptor.capture(), yearCaptor.capture());
        assertThat(monthCaptor.getValue()).isEqualTo(5);
        assertThat(yearCaptor.getValue()).isEqualTo(2025);
    }

    @Test
    void getMonthlySummary_ShouldMapValueObjectToDTO_WhenReturning() {
        // Arrange
        int month = 1;
        int year = 2024;
        MonthlySummary monthlySummary = TestDataFactory.createDefaultMonthlySummary();
        MonthlySummaryResponse response = TestDataFactory.createDefaultMonthlySummaryResponse();

        when(getMonthlySummaryUseCase.execute(month, year)).thenReturn(monthlySummary);
        when(monthlySummaryResponseMapper.toDTO(monthlySummary)).thenReturn(response);

        // Act
        controller.getMonthlySummary(month, year);

        // Assert
        ArgumentCaptor<MonthlySummary> summaryCaptor =
                ArgumentCaptor.forClass(MonthlySummary.class);
        verify(monthlySummaryResponseMapper, times(1)).toDTO(summaryCaptor.capture());
        assertThat(summaryCaptor.getValue()).isEqualTo(monthlySummary);
    }

    @Test
    void getMonthlySummary_ShouldDelegateToUseCase_WithCorrectParams() {
        // Arrange
        int month = 12;
        int year = 2023;
        MonthlySummary monthlySummary = TestDataFactory.createDefaultMonthlySummary();
        MonthlySummaryResponse response = TestDataFactory.createDefaultMonthlySummaryResponse();

        when(getMonthlySummaryUseCase.execute(month, year)).thenReturn(monthlySummary);
        when(monthlySummaryResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.getMonthlySummary(month, year);

        // Assert
        verify(getMonthlySummaryUseCase, times(1)).execute(12, 2023);
    }

    // getCategoriesSummary tests

    @Test
    void getCategoriesSummary_ShouldReturnCategorySummaries_WhenValidParamsProvided() {
        // Arrange
        int month = 1;
        int year = 2024;
        CategorySummary categorySummary1 = TestDataFactory.createIncomeCategorySummary();
        CategorySummary categorySummary2 = TestDataFactory.createExpenseCategorySummary();
        CategoriesSummaryResponse response1 =
                TestDataFactory.createIncomeCategoriesSummaryResponse();
        CategoriesSummaryResponse response2 =
                TestDataFactory.createExpenseCategoriesSummaryResponse();

        when(getCategoriesSummaryUseCase.execute(month, year))
                .thenReturn(List.of(categorySummary1, categorySummary2));
        when(categorySummaryResponseMapper.toListDTO(List.of(categorySummary1, categorySummary2)))
                .thenReturn(List.of(response1, response2));

        // Act
        ResponseEntity<List<CategoriesSummaryResponse>> result =
                controller.getCategoriesSummary(month, year);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).hasSize(2);
        assertThat(result.getBody()).containsExactly(response1, response2);
    }

    @Test
    void getCategoriesSummary_ShouldPassCorrectParams_WhenCalled() {
        // Arrange
        int month = 3;
        int year = 2025;
        CategorySummary categorySummary = TestDataFactory.createIncomeCategorySummary();
        CategoriesSummaryResponse response =
                TestDataFactory.createIncomeCategoriesSummaryResponse();

        when(getCategoriesSummaryUseCase.execute(month, year)).thenReturn(List.of(categorySummary));
        when(categorySummaryResponseMapper.toListDTO(any())).thenReturn(List.of(response));

        // Act
        controller.getCategoriesSummary(month, year);

        // Assert
        ArgumentCaptor<Integer> monthCaptor = ArgumentCaptor.forClass(Integer.class);
        ArgumentCaptor<Integer> yearCaptor = ArgumentCaptor.forClass(Integer.class);
        verify(getCategoriesSummaryUseCase, times(1))
                .execute(monthCaptor.capture(), yearCaptor.capture());
        assertThat(monthCaptor.getValue()).isEqualTo(3);
        assertThat(yearCaptor.getValue()).isEqualTo(2025);
    }

    @Test
    void getCategoriesSummary_ShouldMapListToDTO_WhenReturning() {
        // Arrange
        int month = 1;
        int year = 2024;
        CategorySummary categorySummary = TestDataFactory.createIncomeCategorySummary();
        CategoriesSummaryResponse response =
                TestDataFactory.createIncomeCategoriesSummaryResponse();

        when(getCategoriesSummaryUseCase.execute(month, year)).thenReturn(List.of(categorySummary));
        when(categorySummaryResponseMapper.toListDTO(List.of(categorySummary)))
                .thenReturn(List.of(response));

        // Act
        controller.getCategoriesSummary(month, year);

        // Assert
        verify(categorySummaryResponseMapper, times(1)).toListDTO(List.of(categorySummary));
    }

    @Test
    void getCategoriesSummary_ShouldReturnEmptyList_WhenNoSummariesExist() {
        // Arrange
        int month = 1;
        int year = 2024;

        when(getCategoriesSummaryUseCase.execute(month, year)).thenReturn(Collections.emptyList());
        when(categorySummaryResponseMapper.toListDTO(Collections.emptyList()))
                .thenReturn(Collections.emptyList());

        // Act
        ResponseEntity<List<CategoriesSummaryResponse>> result =
                controller.getCategoriesSummary(month, year);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isEmpty();
        verify(getCategoriesSummaryUseCase, times(1)).execute(month, year);
    }
}
