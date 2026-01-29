package dev.rogerbertan.budget_planner_clean_arch.infra.presentation;

import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Transaction;
import dev.rogerbertan.budget_planner_clean_arch.domain.usecases.*;
import dev.rogerbertan.budget_planner_clean_arch.domain.usecases.transaction.SuggestTransactionCategoryUseCase;
import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.CategorySuggestion;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.CategorySuggestionRequest;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.CategorySuggestionResponse;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.TransactionCreateRequest;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.TransactionResponse;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.TransactionUpdateRequest;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.CategorySuggestionMapper;
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
    private final SuggestTransactionCategoryUseCase suggestTransactionCategoryUseCase;
    private final TransactionResponseMapper transactionResponseMapper;
    private final TransactionCreateMapper transactionCreateMapper;
    private final TransactionUpdateRequestMapper transactionUpdateRequestMapper;
    private final CategorySuggestionMapper categorySuggestionMapper;

    public TransactionController(FindAllTransactionUseCase findAllTransactionsUseCase, FindTransactionByIdUseCase findTransactionByIdUseCase, CreateTransactionUseCase createTransactionUseCase, UpdateTransactionUseCase updateTransactionUseCase, DeleteTransactionUseCase deleteTransactionUseCase, SuggestTransactionCategoryUseCase suggestTransactionCategoryUseCase, TransactionResponseMapper transactionResponseMapper, TransactionCreateMapper transactionCreateMapper, TransactionUpdateRequestMapper transactionUpdateRequestMapper, CategorySuggestionMapper categorySuggestionMapper) {
        this.findAllTransactionsUseCase = findAllTransactionsUseCase;
        this.findTransactionByIdUseCase = findTransactionByIdUseCase;
        this.createTransactionUseCase = createTransactionUseCase;
        this.updateTransactionUseCase = updateTransactionUseCase;
        this.deleteTransactionUseCase = deleteTransactionUseCase;
        this.suggestTransactionCategoryUseCase = suggestTransactionCategoryUseCase;
        this.transactionResponseMapper = transactionResponseMapper;
        this.transactionCreateMapper = transactionCreateMapper;
        this.transactionUpdateRequestMapper = transactionUpdateRequestMapper;
        this.categorySuggestionMapper = categorySuggestionMapper;
    }

    @GetMapping
    public ResponseEntity<Page<TransactionResponse>> getAllTransactions(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size,
            @RequestParam(defaultValue = "createdAt") String sortBy,
            @RequestParam(defaultValue = "DESC") String sortDirection
    ) {
        Sort.Direction direction = Sort.Direction.fromString(sortDirection);
        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortBy));
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

    @PostMapping("/suggest-category")
    public ResponseEntity<CategorySuggestionResponse> suggestCategory(
            @RequestBody CategorySuggestionRequest dto
    ) {
        CategorySuggestion suggestion = suggestTransactionCategoryUseCase.execute(
                dto.description(),
                dto.type()
        );

        return ResponseEntity.ok(categorySuggestionMapper.toDTO(suggestion));
    }
}
