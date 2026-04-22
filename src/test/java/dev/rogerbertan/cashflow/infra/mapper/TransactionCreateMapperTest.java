package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.within;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.infra.dto.TransactionCreateRequest;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;

class TransactionCreateMapperTest {

    private final TransactionCreateMapper mapper = new TransactionCreateMapper();

    @Test
    void toDTO_ShouldMapAllTransactionFieldsToRequest_WhenCalled() {
        Transaction transaction = TestDataFactory.createIncomeTransaction();

        TransactionCreateRequest result = mapper.toDTO(transaction);

        assertThat(result.type()).isEqualTo(transaction.type());
        assertThat(result.amount()).isEqualTo(transaction.amount());
        assertThat(result.description()).isEqualTo(transaction.description());
        assertThat(result.categoryId()).isEqualTo(transaction.category().id());
        assertThat(result.transactionDate()).isEqualTo(transaction.transactionDate());
    }

    @Test
    void toEntity_ShouldMapAllRequestFieldsToTransaction_WhenCalled() {
        TransactionCreateRequest request = TestDataFactory.createIncomeTransactionCreateRequest();

        Transaction result = mapper.toEntity(request);

        assertThat(result.type()).isEqualTo(request.type());
        assertThat(result.amount()).isEqualTo(request.amount());
        assertThat(result.description()).isEqualTo(request.description());
        assertThat(result.transactionDate()).isEqualTo(request.transactionDate());
    }

    @Test
    void toEntity_ShouldCreateCategoryWithOnlyId_WhenMappingFromRequest() {
        TransactionCreateRequest request = TestDataFactory.createIncomeTransactionCreateRequest();

        Transaction result = mapper.toEntity(request);

        assertThat(result.category().id()).isEqualTo(request.categoryId());
        assertThat(result.category().name()).isNull();
        assertThat(result.category().type()).isNull();
        assertThat(result.category().createdAt()).isNull();
    }

    @Test
    void toEntity_ShouldSetCreatedAtToNow_WhenMapping() {
        TransactionCreateRequest request = TestDataFactory.createIncomeTransactionCreateRequest();

        Transaction result = mapper.toEntity(request);

        assertThat(result.createdAt())
                .isCloseTo(LocalDateTime.now(), within(1, ChronoUnit.SECONDS));
    }
}
