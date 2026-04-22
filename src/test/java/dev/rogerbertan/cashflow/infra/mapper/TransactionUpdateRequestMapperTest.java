package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.infra.dto.TransactionUpdateRequest;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;

class TransactionUpdateRequestMapperTest {

    private final TransactionUpdateRequestMapper mapper = new TransactionUpdateRequestMapper();

    @Test
    void toDto_ShouldMapAllTransactionFields_WhenCalled() {
        Transaction transaction = TestDataFactory.createIncomeTransaction();

        TransactionUpdateRequest result = mapper.toDto(transaction);

        assertThat(result.id()).isEqualTo(transaction.id());
        assertThat(result.type()).isEqualTo(transaction.type());
        assertThat(result.amount()).isEqualTo(transaction.amount());
        assertThat(result.description()).isEqualTo(transaction.description());
        assertThat(result.category()).isEqualTo(transaction.category());
        assertThat(result.transactionDate()).isEqualTo(transaction.transactionDate());
        assertThat(result.createdAt()).isEqualTo(transaction.createdAt());
    }

    @Test
    void merge_ShouldPreserveExistingType_WhenDtoTypeIsNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        null, existing.amount(), existing.description(), existing.category());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.type()).isEqualTo(Type.INCOME);
    }

    @Test
    void merge_ShouldUseNewType_WhenDtoTypeIsNotNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        Type.EXPENSE,
                        existing.amount(),
                        existing.description(),
                        existing.category());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.type()).isEqualTo(Type.EXPENSE);
    }

    @Test
    void merge_ShouldPreserveExistingAmount_WhenDtoAmountIsNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        existing.type(), null, existing.description(), existing.category());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.amount()).isEqualTo(existing.amount());
    }

    @Test
    void merge_ShouldUseNewAmount_WhenDtoAmountIsNotNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        BigDecimal newAmount = new BigDecimal("9999.99");
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        existing.type(), newAmount, existing.description(), existing.category());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.amount()).isEqualTo(newAmount);
    }

    @Test
    void merge_ShouldPreserveExistingDescription_WhenDtoDescriptionIsNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        existing.type(), existing.amount(), null, existing.category());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.description()).isEqualTo(existing.description());
    }

    @Test
    void merge_ShouldUseNewDescription_WhenDtoDescriptionIsNotNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        existing.type(), existing.amount(), "New description", existing.category());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.description()).isEqualTo("New description");
    }

    @Test
    void merge_ShouldPreserveExistingCategory_WhenDtoCategoryIsNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        existing.type(), existing.amount(), existing.description(), null);

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.category()).isEqualTo(existing.category());
    }

    @Test
    void merge_ShouldUseNewCategory_WhenDtoCategoryIsNotNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        Category newCategory = TestDataFactory.createExpenseCategory();
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        existing.type(), existing.amount(), existing.description(), newCategory);

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.category()).isEqualTo(newCategory);
    }

    @Test
    void merge_ShouldPreserveExistingTransactionDate_WhenDtoDateIsNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto =
                new TransactionUpdateRequest(
                        existing.id(),
                        existing.type(),
                        existing.amount(),
                        existing.description(),
                        existing.category(),
                        null,
                        existing.createdAt());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.transactionDate()).isEqualTo(existing.transactionDate());
    }

    @Test
    void merge_ShouldUseNewTransactionDate_WhenDtoDateIsNotNull() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        LocalDate newDate = LocalDate.now().minusDays(5);
        TransactionUpdateRequest dto =
                new TransactionUpdateRequest(
                        existing.id(),
                        existing.type(),
                        existing.amount(),
                        existing.description(),
                        existing.category(),
                        newDate,
                        existing.createdAt());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.transactionDate()).isEqualTo(newDate);
    }

    @Test
    void merge_ShouldAlwaysPreserveExistingId_WhenMerging() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        existing.type(),
                        existing.amount(),
                        existing.description(),
                        existing.category());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.id()).isEqualTo(existing.id());
    }

    @Test
    void merge_ShouldAlwaysPreserveExistingCreatedAt_WhenMerging() {
        Transaction existing = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto =
                TestDataFactory.createTransactionUpdateRequest(
                        existing.type(),
                        existing.amount(),
                        existing.description(),
                        existing.category());

        Transaction result = mapper.merge(existing, dto);

        assertThat(result.createdAt()).isEqualTo(existing.createdAt());
    }

    @Test
    void toEntity_ShouldMapAllDtoFields_WhenCalled() {
        Transaction transaction = TestDataFactory.createIncomeTransaction();
        TransactionUpdateRequest dto = mapper.toDto(transaction);

        Transaction result = mapper.toEntity(dto);

        assertThat(result.id()).isEqualTo(dto.id());
        assertThat(result.type()).isEqualTo(dto.type());
        assertThat(result.amount()).isEqualTo(dto.amount());
        assertThat(result.description()).isEqualTo(dto.description());
        assertThat(result.category()).isEqualTo(dto.category());
        assertThat(result.transactionDate()).isEqualTo(dto.transactionDate());
        assertThat(result.createdAt()).isEqualTo(dto.createdAt());
    }
}
