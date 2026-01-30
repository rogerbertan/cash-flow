package dev.rogerbertan.cash_flow.domain.gateway;

import dev.rogerbertan.cash_flow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cash_flow.domain.valueobjects.TransactionAnalysisData;

public interface AIInsightsGateway {
    SpendingInsights generateInsights(TransactionAnalysisData analysisData);
}