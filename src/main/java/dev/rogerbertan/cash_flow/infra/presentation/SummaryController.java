package dev.rogerbertan.cash_flow.infra.presentation;

import dev.rogerbertan.cash_flow.domain.usecases.summary.GetBalanceUseCase;
import dev.rogerbertan.cash_flow.domain.usecases.summary.GetCategoriesSummaryUseCase;
import dev.rogerbertan.cash_flow.domain.usecases.summary.GetMonthlySummaryUseCase;
import dev.rogerbertan.cash_flow.domain.valueobjects.Balance;
import dev.rogerbertan.cash_flow.domain.valueobjects.CategorySummary;
import dev.rogerbertan.cash_flow.domain.valueobjects.MonthlySummary;
import dev.rogerbertan.cash_flow.infra.dto.BalanceResponse;
import dev.rogerbertan.cash_flow.infra.dto.CategoriesSummaryResponse;
import dev.rogerbertan.cash_flow.infra.dto.MonthlySummaryResponse;
import dev.rogerbertan.cash_flow.infra.mapper.BalanceResponseMapper;
import dev.rogerbertan.cash_flow.infra.mapper.CategorySummaryResponseMapper;
import dev.rogerbertan.cash_flow.infra.mapper.MonthlySummaryResponseMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/summary")
public class SummaryController {

    private final GetBalanceUseCase getBalanceUseCase;
    private final GetMonthlySummaryUseCase getMonthlySummaryUseCase;
    private final GetCategoriesSummaryUseCase getCategoriesSummaryUseCase;
    private final BalanceResponseMapper balanceResponseMapper;
    private final MonthlySummaryResponseMapper monthlySummaryResponseMapper;
    private final CategorySummaryResponseMapper categorySummaryResponseMapper;

    public SummaryController(
            GetBalanceUseCase getBalanceUseCase,
            GetMonthlySummaryUseCase getMonthlySummaryUseCase,
            GetCategoriesSummaryUseCase getCategoriesSummaryUseCase,
            BalanceResponseMapper balanceResponseMapper,
            MonthlySummaryResponseMapper monthlySummaryResponseMapper,
            CategorySummaryResponseMapper categorySummaryResponseMapper) {
        this.getBalanceUseCase = getBalanceUseCase;
        this.getMonthlySummaryUseCase = getMonthlySummaryUseCase;
        this.getCategoriesSummaryUseCase = getCategoriesSummaryUseCase;
        this.balanceResponseMapper = balanceResponseMapper;
        this.monthlySummaryResponseMapper = monthlySummaryResponseMapper;
        this.categorySummaryResponseMapper = categorySummaryResponseMapper;
    }

    @GetMapping("/balance")
    public ResponseEntity<BalanceResponse> getBalance() {
        Balance balance = getBalanceUseCase.execute();
        return ResponseEntity.ok(balanceResponseMapper.toDTO(balance));
    }

    @GetMapping("/monthly")
    public ResponseEntity<MonthlySummaryResponse> getMonthlySummary(
            @RequestParam int month,
            @RequestParam int year) {
        MonthlySummary summary = getMonthlySummaryUseCase.execute(month, year);
        return ResponseEntity.ok(monthlySummaryResponseMapper.toDTO(summary));
    }

    @GetMapping("/categories")
    public ResponseEntity<List<CategoriesSummaryResponse>> getCategoriesSummary(
            @RequestParam int month,
            @RequestParam int year) {
        List<CategorySummary> summaries = getCategoriesSummaryUseCase.execute(month, year);
        return ResponseEntity.ok(categorySummaryResponseMapper.toListDTO(summaries));
    }
}