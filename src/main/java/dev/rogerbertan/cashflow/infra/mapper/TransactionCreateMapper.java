package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.infra.dto.TransactionCreateRequest;
import java.time.LocalDateTime;
import org.springframework.stereotype.Component;

@Component
public class TransactionCreateMapper {

    public TransactionCreateRequest toDTO(Transaction transaction) {
        return new TransactionCreateRequest(
                transaction.type(),
                transaction.amount(),
                transaction.description(),
                transaction.category().id(),
                transaction.transactionDate());
    }

    public Transaction toEntity(TransactionCreateRequest dto) {
        Category category = new Category(dto.categoryId(), null, null, null);

        return new Transaction(
                null,
                dto.type(),
                dto.amount(),
                dto.description(),
                category,
                dto.transactionDate(),
                LocalDateTime.now());
    }
}
