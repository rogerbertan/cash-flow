package dev.rogerbertan.budget_planner_clean_arch.infra.mapper;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.TransactionResponse;
import org.springframework.stereotype.Component;

@Component
public class TransactionResponseMapper {

    public TransactionResponse toDTO(Transaction transaction) {
        return new TransactionResponse(
                transaction.id(),
                transaction.type(),
                transaction.amount(),
                transaction.description(),
                transaction.category().id(),
                transaction.transactionDate(),
                transaction.createdAt()
        );
    }

    public Transaction toEntity(TransactionResponse dto) {
        Category category = new Category(
                dto.category(),
                null,
                null,
                null
        );

        return new Transaction(
                dto.id(),
                dto.type(),
                dto.amount(),
                dto.description(),
                category,
                dto.transactionDate(),
                dto.createdAt()
        );
    }
}