package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.infra.persistence.TransactionEntity;
import org.junit.jupiter.api.Test;

class TransactionEntityMapperTest {

    private final CategoryEntityMapper categoryEntityMapper = new CategoryEntityMapper();
    private final TransactionEntityMapper mapper =
            new TransactionEntityMapper(categoryEntityMapper);

    @Test
    void toEntity_ShouldMapAllTransactionFieldsToEntity_WhenCalled() {
        Transaction transaction = TestDataFactory.createIncomeTransaction();

        TransactionEntity result = mapper.toEntity(transaction);

        assertThat(result.getType()).isEqualTo(transaction.type());
        assertThat(result.getAmount()).isEqualTo(transaction.amount());
        assertThat(result.getDescription()).isEqualTo(transaction.description());
        assertThat(result.getTransactionDate()).isEqualTo(transaction.transactionDate());
    }

    @Test
    void toEntity_ShouldDelegateCategoryMappingToCategoryEntityMapper_WhenCalled() {
        Transaction transaction = TestDataFactory.createIncomeTransaction();

        TransactionEntity result = mapper.toEntity(transaction);

        assertThat(result.getCategory()).isNotNull();
        assertThat(result.getCategory().getName()).isEqualTo(transaction.category().name());
        assertThat(result.getCategory().getType()).isEqualTo(transaction.category().type());
    }

    @Test
    void toDomain_ShouldMapAllEntityFieldsToDomain_WhenCalled() {
        TransactionEntity entity = TestDataFactory.createIncomeTransactionEntity();

        Transaction result = mapper.toDomain(entity);

        assertThat(result.id()).isEqualTo(entity.getId());
        assertThat(result.type()).isEqualTo(entity.getType());
        assertThat(result.amount()).isEqualTo(entity.getAmount());
        assertThat(result.description()).isEqualTo(entity.getDescription());
        assertThat(result.transactionDate()).isEqualTo(entity.getTransactionDate());
        assertThat(result.createdAt()).isEqualTo(entity.getCreatedAt());
    }

    @Test
    void toDomain_ShouldDelegateCategoryMappingToCategoryEntityMapper_WhenCalled() {
        TransactionEntity entity = TestDataFactory.createIncomeTransactionEntity();

        Transaction result = mapper.toDomain(entity);

        assertThat(result.category()).isNotNull();
        assertThat(result.category().id()).isEqualTo(entity.getCategory().getId());
        assertThat(result.category().name()).isEqualTo(entity.getCategory().getName());
        assertThat(result.category().type()).isEqualTo(entity.getCategory().getType());
    }
}
