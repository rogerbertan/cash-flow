package dev.rogerbertan.budget_planner_clean_arch.infra.mapper;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.TransactionEntity;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionEntityMapper {

    private final CategoryEntityMapper categoryEntityMapper;

    public TransactionEntityMapper(CategoryEntityMapper categoryEntityMapper) {
        this.categoryEntityMapper = categoryEntityMapper;
    }

    public TransactionEntity toEntity(Transaction transaction) {
        return new TransactionEntity(
                transaction.type(),
                transaction.amount(),
                transaction.description(),
                categoryEntityMapper.toEntity(transaction.category()),
                transaction.transactionDate()
        );
    }

    public Transaction toDomain(TransactionEntity entity) {
        return new Transaction(
                entity.getId(),
                entity.getType(),
                entity.getAmount(),
                entity.getDescription(),
                categoryEntityMapper.toDomain(entity.getCategory()),
                entity.getTransactionDate(),
                entity.getCreatedAt()
        );
    }
}