package dev.rogerbertan.cashflow.application.gateway;

import dev.rogerbertan.cashflow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cashflow.domain.valueobjects.TransactionAnalysisData;

public interface AIInsightsGateway {
    SpendingInsights generateInsights(TransactionAnalysisData analysisData);
}
