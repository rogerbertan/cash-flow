package dev.rogerbertan.budget_planner_clean_arch.infra.mapper;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.TransactionCreateRequest;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
public class TransactionCreateMapper {

    public TransactionCreateRequest toDTO(Transaction transaction) {
        return new TransactionCreateRequest(
                transaction.type(),
                transaction.amount(),
                transaction.description(),
                transaction.category().id(),
                transaction.transactionDate()
        );
    }

    public Transaction toEntity(TransactionCreateRequest dto) {
        Category category = new Category(
                dto.categoryId(),
                null,
                null,
                null
        );

        return new Transaction(
                null,
                dto.type(),
                dto.amount(),
                dto.description(),
                category,
                dto.transactionDate(),
                LocalDateTime.now()
        );
    }
}