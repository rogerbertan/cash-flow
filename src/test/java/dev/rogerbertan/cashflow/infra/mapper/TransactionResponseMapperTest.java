package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.infra.dto.TransactionResponse;
import org.junit.jupiter.api.Test;

class TransactionResponseMapperTest {

    private final TransactionResponseMapper mapper = new TransactionResponseMapper();

    @Test
    void toDTO_ShouldMapAllTransactionFieldsToResponse_WhenCalled() {
        Transaction transaction = TestDataFactory.createIncomeTransaction();

        TransactionResponse result = mapper.toDTO(transaction);

        assertThat(result.id()).isEqualTo(transaction.id());
        assertThat(result.type()).isEqualTo(transaction.type());
        assertThat(result.amount()).isEqualTo(transaction.amount());
        assertThat(result.description()).isEqualTo(transaction.description());
        assertThat(result.transactionDate()).isEqualTo(transaction.transactionDate());
        assertThat(result.createdAt()).isEqualTo(transaction.createdAt());
    }

    @Test
    void toDTO_ShouldUseCategoryIdFromNestedCategory_WhenMapping() {
        Transaction transaction = TestDataFactory.createIncomeTransaction();

        TransactionResponse result = mapper.toDTO(transaction);

        assertThat(result.category()).isEqualTo(transaction.category().id());
    }

    @Test
    void toEntity_ShouldMapAllResponseFieldsToTransaction_WhenCalled() {
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        Transaction result = mapper.toEntity(response);

        assertThat(result.id()).isEqualTo(response.id());
        assertThat(result.type()).isEqualTo(response.type());
        assertThat(result.amount()).isEqualTo(response.amount());
        assertThat(result.description()).isEqualTo(response.description());
        assertThat(result.transactionDate()).isEqualTo(response.transactionDate());
        assertThat(result.createdAt()).isEqualTo(response.createdAt());
    }

    @Test
    void toEntity_ShouldCreateCategoryWithOnlyId_WhenMappingFromResponse() {
        TransactionResponse response = TestDataFactory.createIncomeTransactionResponse();

        Transaction result = mapper.toEntity(response);

        assertThat(result.category().id()).isEqualTo(response.category());
        assertThat(result.category().name()).isNull();
        assertThat(result.category().type()).isNull();
        assertThat(result.category().createdAt()).isNull();
    }
}
