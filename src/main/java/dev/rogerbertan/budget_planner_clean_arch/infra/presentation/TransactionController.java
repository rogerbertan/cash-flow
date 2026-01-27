package dev.rogerbertan.budget_planner_clean_arch.infra.presentation;

import dev.rogerbertan.budget_planner_clean_arch.domain.usecases.*;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.TransactionCreateMapper;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.TransactionResponseMapper;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.TransactionUpdateRequestMapper;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/transactions")
public class TransactionController {

    private final FindAllTransactionUseCase findAllTransactionsUseCase;
    private final FindTransactionByIdUseCase findTransactionByIdUseCase;
    private final CreateTransactionUseCase createTransactionUseCase;
    private final UpdateTransactionUseCase updateTransactionUseCase;
    private final DeleteTransactionUseCase deleteTransactionUseCase;
    private final TransactionResponseMapper transactionResponseMapper;
    private final TransactionCreateMapper transactionCreateMapper;
    private final TransactionUpdateRequestMapper transactionUpdateRequestMapper;

    public TransactionController(FindAllTransactionUseCase findAllTransactionsUseCase, FindTransactionByIdUseCase findTransactionByIdUseCase, CreateTransactionUseCase createTransactionUseCase, UpdateTransactionUseCase updateTransactionUseCase, DeleteTransactionUseCase deleteTransactionUseCase, TransactionResponseMapper transactionResponseMapper, TransactionCreateMapper transactionCreateMapper, TransactionUpdateRequestMapper transactionUpdateRequestMapper) {
        this.findAllTransactionsUseCase = findAllTransactionsUseCase;
        this.findTransactionByIdUseCase = findTransactionByIdUseCase;
        this.createTransactionUseCase = createTransactionUseCase;
        this.updateTransactionUseCase = updateTransactionUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionCreateMapper = transactionCreateMapper;
        this.transactionUpdateRequestMapper = transactionUpdateRequestMapper;
    }
}
