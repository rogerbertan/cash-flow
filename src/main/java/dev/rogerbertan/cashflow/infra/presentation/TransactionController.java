package dev.rogerbertan.cashflow.infra.presentation;

import dev.rogerbertan.cashflow.domain.entities.Transaction;
import dev.rogerbertan.cashflow.domain.usecases.transaction.CreateTransactionUseCase;
import dev.rogerbertan.cashflow.domain.usecases.transaction.DeleteTransactionUseCase;
import dev.rogerbertan.cashflow.domain.usecases.transaction.FindAllTransactionUseCase;
import dev.rogerbertan.cashflow.domain.usecases.transaction.FindTransactionByIdUseCase;
import dev.rogerbertan.cashflow.domain.usecases.transaction.SuggestTransactionCategoryUseCase;
import dev.rogerbertan.cashflow.domain.usecases.transaction.UpdateTransactionUseCase;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySuggestion;
import dev.rogerbertan.cashflow.infra.dto.CategorySuggestionRequest;
import dev.rogerbertan.cashflow.infra.dto.CategorySuggestionResponse;
import dev.rogerbertan.cashflow.infra.dto.TransactionCreateRequest;
import dev.rogerbertan.cashflow.infra.dto.TransactionResponse;
import dev.rogerbertan.cashflow.infra.dto.TransactionUpdateRequest;
import dev.rogerbertan.cashflow.infra.mapper.CategorySuggestionMapper;
import dev.rogerbertan.cashflow.infra.mapper.TransactionCreateMapper;
import dev.rogerbertan.cashflow.infra.mapper.TransactionResponseMapper;
import dev.rogerbertan.cashflow.infra.mapper.TransactionUpdateRequestMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

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

    public TransactionController(
            FindAllTransactionUseCase findAllTransactionsUseCase,
            FindTransactionByIdUseCase findTransactionByIdUseCase,
            CreateTransactionUseCase createTransactionUseCase,
            UpdateTransactionUseCase updateTransactionUseCase,
            DeleteTransactionUseCase deleteTransactionUseCase,
            SuggestTransactionCategoryUseCase suggestTransactionCategoryUseCase,
            TransactionResponseMapper transactionResponseMapper,
            TransactionCreateMapper transactionCreateMapper,
            TransactionUpdateRequestMapper transactionUpdateRequestMapper,
            CategorySuggestionMapper categorySuggestionMapper) {
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
            @RequestParam(defaultValue = "DESC") String sortDirection) {
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
    public ResponseEntity<TransactionResponse> createTransaction(
            @RequestBody TransactionCreateRequest request) {

        Transaction created =
                createTransactionUseCase.execute(transactionCreateMapper.toEntity(request));
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(transactionResponseMapper.toDTO(created));
    }

    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> updateTransaction(
            @PathVariable Long id, @RequestBody TransactionUpdateRequest request) {

        Transaction existingTransaction = findTransactionByIdUseCase.execute(id);
        if (existingTransaction == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }

        Transaction updatedTransaction =
                updateTransactionUseCase.execute(
                        transactionUpdateRequestMapper.merge(existingTransaction, request));
        return ResponseEntity.ok(transactionResponseMapper.toDTO(updatedTransaction));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTransaction(@PathVariable Long id) {

        deleteTransactionUseCase.execute(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/suggest-category")
    public ResponseEntity<CategorySuggestionResponse> suggestCategory(
            @RequestBody CategorySuggestionRequest dto) {
        CategorySuggestion suggestion =
                suggestTransactionCategoryUseCase.execute(dto.description(), dto.type());

        return ResponseEntity.ok(categorySuggestionMapper.toDTO(suggestion));
    }
}
