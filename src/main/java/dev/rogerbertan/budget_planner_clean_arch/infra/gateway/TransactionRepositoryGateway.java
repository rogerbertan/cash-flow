package dev.rogerbertan.budget_planner_clean_arch.infra.gateway;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.TransactionEntityMapper;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.TransactionEntity;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.TransactionRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

@Component
public class TransactionRepositoryGateway implements TransactionGateway {

    private final TransactionRepository transactionRepository;
    private final TransactionEntityMapper entityMapper;

    public TransactionRepositoryGateway(TransactionRepository transactionRepository, TransactionEntityMapper entityMapper) {
        this.transactionRepository = transactionRepository;
        this.entityMapper = entityMapper;
    }

    @Override
    public Page<Transaction> findAllTransactions(Pageable pageable) {

        Page<TransactionEntity> entities = transactionRepository.findAll(pageable);
        return entities.map(entityMapper::toDomain);
    }

    @Override
    public Transaction findTransactionById(Long id) {

        return transactionRepository.findById(id)
                .map(entityMapper::toDomain)
                .orElse(null);
    }

    @Override
    public Transaction createTransaction(Transaction transaction) {

        TransactionEntity savedEntity = transactionRepository.save(entityMapper.toEntity(transaction));

        return entityMapper.toDomain(savedEntity);
    }

    @Override
    public Transaction updateTransaction(Transaction transaction) {

        TransactionEntity updatedEntity = transactionRepository.save(entityMapper.toEntity(transaction));

        return entityMapper.toDomain(updatedEntity);
    }

    @Override
    public void deleteTransaction(Long id) {

        transactionRepository.deleteById(id);
    }
}
