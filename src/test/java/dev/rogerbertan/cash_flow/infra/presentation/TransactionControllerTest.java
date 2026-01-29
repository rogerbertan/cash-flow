package dev.rogerbertan.cash_flow.infra.presentation;

import dev.rogerbertan.cash_flow.domain.entities.Category;
import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.domain.enums.Type;
import dev.rogerbertan.cash_flow.domain.usecases.*;
import dev.rogerbertan.cash_flow.infra.dto.TransactionCreateRequest;
import dev.rogerbertan.cash_flow.infra.dto.TransactionResponse;
import dev.rogerbertan.cash_flow.infra.dto.TransactionUpdateRequest;
import dev.rogerbertan.cash_flow.infra.mapper.TransactionCreateMapper;
import dev.rogerbertan.cash_flow.infra.mapper.TransactionResponseMapper;
import dev.rogerbertan.cash_flow.infra.mapper.TransactionUpdateRequestMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransactionControllerTest {

    @Mock
    private FindAllTransactionUseCase findAllTransactionUseCase;

    @Mock
    private FindTransactionByIdUseCase findTransactionByIdUseCase;

    @Mock
    private CreateTransactionUseCase createTransactionUseCase;

    @Mock
    private UpdateTransactionUseCase updateTransactionUseCase;

    @Mock
    private DeleteTransactionUseCase deleteTransactionUseCase;

    @Mock
    private TransactionResponseMapper transactionResponseMapper;

    @Mock
    private TransactionCreateMapper transactionCreateMapper;

    @Mock
    private TransactionUpdateRequestMapper transactionUpdateRequestMapper;

    @InjectMocks
    private TransactionController controller;

    // getAllTransactions tests (pagination)

    @Test
    void getAllTransactions_ShouldReturnPagedResults_WhenTransactionsExist() {
        // Arrange
        Transaction transaction1 = TestDataFactory.createIncomeTransaction();
        Transaction transaction2 = TestDataFactory.createExpenseTransaction();
        TransactionResponse response1 = TestDataFactory.createIncomeTransactionResponse();
        TransactionResponse response2 = TestDataFactory.createExpenseTransactionResponse();

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction1, transaction2), pageable, 2);

        when(findAllTransactionUseCase.execute(any(Pageable.class))).thenReturn(transactionPage);
        when(transactionResponseMapper.toDTO(transaction1)).thenReturn(response1);
        when(transactionResponseMapper.toDTO(transaction2)).thenReturn(response2);

        // Act
        ResponseEntity<Page<TransactionResponse>> result = controller.getAllTransactions(0, 20, "createdAt", "DESC");

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody().getContent()).hasSize(2);
        assertThat(result.getBody().getTotalElements()).isEqualTo(2);
        assertThat(result.getBody().getNumber()).isEqualTo(0);
        assertThat(result.getBody().getSize()).isEqualTo(20);
    }

    @Test
    void getAllTransactions_ShouldUseDefaultPagination_WhenNoParamsProvided() {
        // Arrange
        Transaction transaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction), pageable, 1);

        when(findAllTransactionUseCase.execute(any(Pageable.class))).thenReturn(transactionPage);
        when(transactionResponseMapper.toDTO(transaction)).thenReturn(response);

        // Act
        controller.getAllTransactions(0, 20, "createdAt", "DESC");

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(findAllTransactionUseCase, times(1)).execute(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(0);
        assertThat(capturedPageable.getPageSize()).isEqualTo(20);
        assertThat(capturedPageable.getSort().getOrderFor("createdAt")).isNotNull();
        assertThat(capturedPageable.getSort().getOrderFor("createdAt").getDirection()).isEqualTo(Sort.Direction.DESC);
    }

    @Test
    void getAllTransactions_ShouldUseCustomPagination_WhenParamsProvided() {
        // Arrange
        Transaction transaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        Pageable pageable = PageRequest.of(2, 10, Sort.by(Sort.Direction.ASC, "amount"));
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction), pageable, 25);

        when(findAllTransactionUseCase.execute(any(Pageable.class))).thenReturn(transactionPage);
        when(transactionResponseMapper.toDTO(transaction)).thenReturn(response);

        // Act
        controller.getAllTransactions(2, 10, "amount", "ASC");

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(findAllTransactionUseCase, times(1)).execute(pageableCaptor.capture());

        Pageable capturedPageable = pageableCaptor.getValue();
        assertThat(capturedPageable.getPageNumber()).isEqualTo(2);
        assertThat(capturedPageable.getPageSize()).isEqualTo(10);
        assertThat(capturedPageable.getSort().getOrderFor("amount")).isNotNull();
        assertThat(capturedPageable.getSort().getOrderFor("amount").getDirection()).isEqualTo(Sort.Direction.ASC);
    }

    @Test
    void getAllTransactions_ShouldMapPageContent_WhenTransactionsExist() {
        // Arrange
        Transaction transaction1 = TestDataFactory.createIncomeTransaction();
        Transaction transaction2 = TestDataFactory.createExpenseTransaction();
        TransactionResponse response1 = TestDataFactory.createIncomeTransactionResponse();
        TransactionResponse response2 = TestDataFactory.createExpenseTransactionResponse();

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction1, transaction2), pageable, 2);

        when(findAllTransactionUseCase.execute(any(Pageable.class))).thenReturn(transactionPage);
        when(transactionResponseMapper.toDTO(transaction1)).thenReturn(response1);
        when(transactionResponseMapper.toDTO(transaction2)).thenReturn(response2);

        // Act
        ResponseEntity<Page<TransactionResponse>> result = controller.getAllTransactions(0, 20, "createdAt", "DESC");

        // Assert
        verify(transactionResponseMapper, times(1)).toDTO(transaction1);
        verify(transactionResponseMapper, times(1)).toDTO(transaction2);
        assertThat(result.getBody().getContent()).containsExactly(response1, response2);
    }

    @Test
    void getAllTransactions_ShouldHandleSortDirection_WhenASCProvided() {
        // Arrange
        Transaction transaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        Pageable pageable = PageRequest.of(0, 20, Sort.by(Sort.Direction.ASC, "createdAt"));
        Page<Transaction> transactionPage = new PageImpl<>(List.of(transaction), pageable, 1);

        when(findAllTransactionUseCase.execute(any(Pageable.class))).thenReturn(transactionPage);
        when(transactionResponseMapper.toDTO(transaction)).thenReturn(response);

        // Act
        controller.getAllTransactions(0, 20, "createdAt", "ASC");

        // Assert
        ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
        verify(findAllTransactionUseCase).execute(pageableCaptor.capture());

        assertThat(pageableCaptor.getValue().getSort().getOrderFor("createdAt").getDirection())
                .isEqualTo(Sort.Direction.ASC);
    }

    // getTransactionById tests

    @Test
    void getTransactionById_ShouldReturnTransaction_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Transaction transaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        when(findTransactionByIdUseCase.execute(transactionId)).thenReturn(transaction);
        when(transactionResponseMapper.toDTO(transaction)).thenReturn(response);

        // Act
        ResponseEntity<TransactionResponse> result = controller.getTransactionById(transactionId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().id()).isEqualTo(1L);
        assertThat(result.getBody().type()).isEqualTo(Type.INCOME);
        assertThat(result.getBody().amount()).isEqualByComparingTo(new BigDecimal("1000.00"));
    }

    @Test
    void getTransactionById_ShouldCallMapperOnce_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Transaction transaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        when(findTransactionByIdUseCase.execute(transactionId)).thenReturn(transaction);
        when(transactionResponseMapper.toDTO(transaction)).thenReturn(response);

        // Act
        controller.getTransactionById(transactionId);

        // Assert
        verify(transactionResponseMapper, times(1)).toDTO(transaction);
    }

    @Test
    void getTransactionById_ShouldDelegateToUseCase_WithCorrectId() {
        // Arrange
        Long transactionId = 123L;
        Transaction transaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        when(findTransactionByIdUseCase.execute(transactionId)).thenReturn(transaction);
        when(transactionResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.getTransactionById(transactionId);

        // Assert
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(findTransactionByIdUseCase, times(1)).execute(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(123L);
    }

    // createTransaction tests

    @Test
    void createTransaction_ShouldReturnCreatedTransaction_WhenValidRequestProvided() {
        // Arrange
        TransactionCreateRequest request = TestDataFactory.createIncomeTransactionCreateRequest();
        Transaction inputTransaction = TestDataFactory.createTransaction(
                null,
                TestDataFactory.createIncomeCategory(),
                new BigDecimal("1000.00")
        );
        Transaction savedTransaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        when(transactionCreateMapper.toEntity(request)).thenReturn(inputTransaction);
        when(createTransactionUseCase.execute(inputTransaction)).thenReturn(savedTransaction);
        when(transactionResponseMapper.toDTO(savedTransaction)).thenReturn(response);

        // Act
        ResponseEntity<TransactionResponse> result = controller.createTransaction(request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().id()).isEqualTo(1L);
        assertThat(result.getBody().type()).isEqualTo(Type.INCOME);
    }

    @Test
    void createTransaction_ShouldReturn201Status_WhenTransactionCreated() {
        // Arrange
        TransactionCreateRequest request = TestDataFactory.createIncomeTransactionCreateRequest();
        Transaction inputTransaction = TestDataFactory.createTransaction(null, TestDataFactory.createIncomeCategory(), new BigDecimal("1000.00"));
        Transaction savedTransaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        when(transactionCreateMapper.toEntity(request)).thenReturn(inputTransaction);
        when(createTransactionUseCase.execute(any())).thenReturn(savedTransaction);
        when(transactionResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        ResponseEntity<TransactionResponse> result = controller.createTransaction(request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.CREATED);
    }

    @Test
    void createTransaction_ShouldMapRequestToDomain_WhenCreating() {
        // Arrange
        TransactionCreateRequest request = TestDataFactory.createIncomeTransactionCreateRequest();
        Transaction inputTransaction = TestDataFactory.createTransaction(null, TestDataFactory.createIncomeCategory(), new BigDecimal("1000.00"));
        Transaction savedTransaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        when(transactionCreateMapper.toEntity(request)).thenReturn(inputTransaction);
        when(createTransactionUseCase.execute(any())).thenReturn(savedTransaction);
        when(transactionResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.createTransaction(request);

        // Assert
        ArgumentCaptor<TransactionCreateRequest> requestCaptor = ArgumentCaptor.forClass(TransactionCreateRequest.class);
        verify(transactionCreateMapper, times(1)).toEntity(requestCaptor.capture());
        assertThat(requestCaptor.getValue()).isEqualTo(request);
    }

    @Test
    void createTransaction_ShouldDelegateToUseCase_WhenCalled() {
        // Arrange
        TransactionCreateRequest request = TestDataFactory.createIncomeTransactionCreateRequest();
        Transaction inputTransaction = TestDataFactory.createTransaction(null, TestDataFactory.createIncomeCategory(), new BigDecimal("1000.00"));
        Transaction savedTransaction = TestDataFactory.createIncomeTransaction();
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        when(transactionCreateMapper.toEntity(request)).thenReturn(inputTransaction);
        when(createTransactionUseCase.execute(inputTransaction)).thenReturn(savedTransaction);
        when(transactionResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.createTransaction(request);

        // Assert
        verify(createTransactionUseCase, times(1)).execute(inputTransaction);
    }

    // updateTransaction tests

    @Test
    void updateTransaction_ShouldReturnUpdatedTransaction_WhenTransactionExists() {
        // Arrange
        Long transactionId = 1L;
        Category category = TestDataFactory.createIncomeCategory();
        TransactionUpdateRequest request = TestDataFactory.createTransactionUpdateRequest(
                Type.INCOME,
                new BigDecimal("1500.00"),
                "Updated description",
                category
        );
        Transaction existingTransaction = TestDataFactory.createIncomeTransaction();
        Transaction mergedTransaction = TestDataFactory.createTransaction(1L, category, new BigDecimal("1500.00"));
        Transaction updatedTransaction = TestDataFactory.createTransaction(1L, category, new BigDecimal("1500.00"));
        TransactionResponse response = TestDataFactory.createTransactionResponse(
                1L,
                Type.INCOME,
                new BigDecimal("1500.00"),
                "Updated description",
                1L,
                existingTransaction.transactionDate(),
                existingTransaction.createdAt()
        );

        when(findTransactionByIdUseCase.execute(transactionId)).thenReturn(existingTransaction);
        when(transactionUpdateRequestMapper.merge(existingTransaction, request)).thenReturn(mergedTransaction);
        when(updateTransactionUseCase.execute(mergedTransaction)).thenReturn(updatedTransaction);
        when(transactionResponseMapper.toDTO(updatedTransaction)).thenReturn(response);

        // Act
        ResponseEntity<TransactionResponse> result = controller.updateTransaction(transactionId, request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(result.getBody()).isNotNull();
        assertThat(result.getBody()).isEqualTo(response);
        assertThat(result.getBody().amount()).isEqualByComparingTo(new BigDecimal("1500.00"));
    }

    @Test
    void updateTransaction_ShouldReturn404_WhenTransactionDoesNotExist() {
        // Arrange
        Long transactionId = 999L;
        TransactionUpdateRequest request = TestDataFactory.createTransactionUpdateRequest(
                Type.INCOME,
                new BigDecimal("1000.00"),
                "Test",
                TestDataFactory.createIncomeCategory()
        );

        when(findTransactionByIdUseCase.execute(transactionId)).thenReturn(null);

        // Act
        ResponseEntity<TransactionResponse> result = controller.updateTransaction(transactionId, request);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(result.getBody()).isNull();
        verify(findTransactionByIdUseCase, times(1)).execute(transactionId);
        verify(updateTransactionUseCase, never()).execute(any());
        verify(transactionResponseMapper, never()).toDTO(any());
    }

    @Test
    void updateTransaction_ShouldMergeExistingAndRequest_WhenUpdating() {
        // Arrange
        Long transactionId = 1L;
        Category category = TestDataFactory.createIncomeCategory();
        TransactionUpdateRequest request = TestDataFactory.createTransactionUpdateRequest(
                Type.INCOME,
                new BigDecimal("1500.00"),
                "Updated",
                category
        );
        Transaction existingTransaction = TestDataFactory.createIncomeTransaction();
        Transaction mergedTransaction = TestDataFactory.createTransaction(1L, category, new BigDecimal("1500.00"));
        Transaction updatedTransaction = TestDataFactory.createTransaction(1L, category, new BigDecimal("1500.00"));
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        when(findTransactionByIdUseCase.execute(transactionId)).thenReturn(existingTransaction);
        when(transactionUpdateRequestMapper.merge(existingTransaction, request)).thenReturn(mergedTransaction);
        when(updateTransactionUseCase.execute(any())).thenReturn(updatedTransaction);
        when(transactionResponseMapper.toDTO(any())).thenReturn(response);

        // Act
        controller.updateTransaction(transactionId, request);

        // Assert
        ArgumentCaptor<Transaction> existingCaptor = ArgumentCaptor.forClass(Transaction.class);
        ArgumentCaptor<TransactionUpdateRequest> requestCaptor = ArgumentCaptor.forClass(TransactionUpdateRequest.class);
        verify(transactionUpdateRequestMapper, times(1)).merge(existingCaptor.capture(), requestCaptor.capture());
        assertThat(existingCaptor.getValue()).isEqualTo(existingTransaction);
        assertThat(requestCaptor.getValue()).isEqualTo(request);
    }

    // deleteTransaction tests

    @Test
    void deleteTransaction_ShouldReturn204_WhenTransactionDeleted() {
        // Arrange
        Long transactionId = 1L;
        doNothing().when(deleteTransactionUseCase).execute(transactionId);

        // Act
        ResponseEntity<Void> result = controller.deleteTransaction(transactionId);

        // Assert
        assertThat(result.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        assertThat(result.getBody()).isNull();
        verify(deleteTransactionUseCase, times(1)).execute(transactionId);
    }

    @Test
    void deleteTransaction_ShouldDelegateToUseCase_WhenCalled() {
        // Arrange
        Long transactionId = 123L;
        doNothing().when(deleteTransactionUseCase).execute(transactionId);

        // Act
        controller.deleteTransaction(transactionId);

        // Assert
        ArgumentCaptor<Long> idCaptor = ArgumentCaptor.forClass(Long.class);
        verify(deleteTransactionUseCase, times(1)).execute(idCaptor.capture());
        assertThat(idCaptor.getValue()).isEqualTo(123L);
    }
}
