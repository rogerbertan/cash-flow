package dev.rogerbertan.budget_planner_clean_arch.infra.presentation;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.usecases.*;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.TransactionCreateRequest;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.TransactionResponse;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.TransactionUpdateRequest;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.TransactionCreateMapper;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.TransactionResponseMapper;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.TransactionUpdateRequestMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt,desc") String[] sort
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(sort));
        Page<Transaction> transactions = findAllTransactionsUseCase.execute(pageable);
        return ResponseEntity.ok(transactions.map(transactionResponseMapper::toDTO));
    }

    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getTransactionById(@PathVariable Long id) {

        Transaction transaction = findTransactionByIdUseCase.execute(id);
        return ResponseEntity.ok(transactionResponseMapper.toDTO(transaction));
    }

    @PostMapping
    public ResponseEntity<TransactionResponse> createTransaction(@RequestBody TransactionCreateRequest request) {

        Transaction created = createTransactionUseCase.execute(transactionCreateMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED).body(transactionResponseMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id,
            @RequestBody TransactionUpdateRequest request) {

        Transaction existingTransaction = findTransactionByIdUseCase.execute(id);
        if (existingTransaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Transaction updatedTransaction = updateTransactionUseCase.execute(transactionUpdateRequestMapper.merge(existingTransaction, request));
        return ResponseEntity.ok(transactionResponseMapper.toDTO(updatedTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {

        deleteTransactionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }
}
