package dev.rogerbertan.cash_flow.infra.mapper;

import dev.rogerbertan.cash_flow.domain.entities.Transaction;
import dev.rogerbertan.cash_flow.infra.dto.TransactionUpdateRequest;
import org.springframework.stereotype.Component;

@Component
public class TransactionUpdateRequestMapper {

    public TransactionUpdateRequest toDto(Transaction transaction) {
        return new TransactionUpdateRequest(
                transaction.id(),
                transaction.type(),
                transaction.amount(),
                transaction.description(),
                transaction.category(),
                transaction.transactionDate(),
                transaction.createdAt()
        );
    }

    public Transaction merge(Transaction existentTransaction, TransactionUpdateRequest dto) {
        return new Transaction(
                existentTransaction.id(),
                dto.type() != null ? dto.type() : existentTransaction.type(),
                dto.amount() != null ? dto.amount() : existentTransaction.amount(),
                dto.description() != null ? dto.description() : existentTransaction.description(),
                dto.category() != null ? dto.category() : existentTransaction.category(),
                dto.transactionDate() != null ? dto.transactionDate() : existentTransaction.transactionDate(),
                existentTransaction.createdAt()
        );
    }

    public Transaction toEntity(TransactionUpdateRequest dto) {
        return new Transaction(
                dto.id(),
                dto.type(),
                dto.amount(),
                dto.description(),
                dto.category(),
                dto.transactionDate(),
                dto.createdAt()
        );
    }
}
