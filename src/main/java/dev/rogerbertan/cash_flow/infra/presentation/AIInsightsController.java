package dev.rogerbertan.cash_flow.infra.presentation;

import dev.rogerbertan.cash_flow.domain.usecases.insights.GenerateSpendingInsightsUseCase;
import dev.rogerbertan.cash_flow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cash_flow.infra.dto.SpendingInsightsResponse;
import dev.rogerbertan.cash_flow.infra.mapper.SpendingInsightsMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/ai")
public class AIInsightsController {

    private final GenerateSpendingInsightsUseCase generateSpendingInsightsUseCase;
    private final SpendingInsightsMapper spendingInsightsMapper;

    public AIInsightsController(
            GenerateSpendingInsightsUseCase generateSpendingInsightsUseCase,
            SpendingInsightsMapper spendingInsightsMapper
    ) {
        this.generateSpendingInsightsUseCase = generateSpendingInsightsUseCase;
        this.spendingInsightsMapper = spendingInsightsMapper;
    }

    @GetMapping("/insights")
    public ResponseEntity<SpendingInsightsResponse> getSpendingInsights(
            @RequestParam(defaultValue = "monthly") String period
    ) {
        SpendingInsights insights = generateSpendingInsightsUseCase.execute(period);
        return ResponseEntity.ok(spendingInsightsMapper.toDTO(insights));
    }
}