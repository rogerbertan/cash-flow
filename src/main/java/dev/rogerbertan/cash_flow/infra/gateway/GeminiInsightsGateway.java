package dev.rogerbertan.cash_flow.infra.gateway;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import dev.rogerbertan.cash_flow.domain.gateway.AIInsightsGateway;
import dev.rogerbertan.cash_flow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cash_flow.domain.valueobjects.TransactionAnalysisData;
import dev.rogerbertan.cash_flow.infra.config.AIProperties;
import dev.rogerbertan.cash_flow.infra.exception.AIInsightsException;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.util.Arrays;
import java.util.List;

public class GeminiInsightsGateway implements AIInsightsGateway {

    private final AIProperties aiProperties;
    private final Client geminiClient;

    public GeminiInsightsGateway(AIProperties aiProperties) {
        this.aiProperties = aiProperties;
        System.out.println("Using Gemini model for insights: " + aiProperties.getModelName());

        if (aiProperties.isEnabled() && (aiProperties.getApiKey() != null && !aiProperties.getApiKey().isEmpty())) {
            this.geminiClient = initializeClient();
        } else {
            this.geminiClient = null;
        }
    }

    private Client initializeClient() {
        try {
            if (aiProperties.getApiKey() == null || aiProperties.getApiKey().isEmpty()) {
                throw new AIInsightsException("Gemini API key is not configured. Set GOOGLE_API_KEY environment variable.");
            }
            return Client.builder().apiKey(aiProperties.getApiKey()).build();
        } catch (Exception e) {
            throw new AIInsightsException("Failed to initialize Gemini client for insights", e);
        }
    }

    @Override
    public SpendingInsights generateInsights(TransactionAnalysisData analysisData) {
        if (!aiProperties.isEnabled() || geminiClient == null) {
            return new SpendingInsights(
                    List.of("AI insights are disabled"),
                    analysisData.period(),
                    "AI analysis unavailable",
                    "disabled"
            );
        }

        String prompt = buildPrompt(analysisData);

        try {
            String aiResponse = callGeminiAPI(prompt);
            List<String> insights = parseInsights(aiResponse);
            String summary = generateSummary(insights, aiResponse);

            return new SpendingInsights(insights, analysisData.period(), summary, aiResponse);
        } catch (Exception e) {
            throw new AIInsightsException("Failed to generate spending insights: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(TransactionAnalysisData data) {
        StringBuilder prompt = new StringBuilder();

        prompt.append("You are a personal finance advisor analyzing spending patterns. ");
        prompt.append("Based on the transaction data below, provide 5-8 actionable insights about spending behavior.\n\n");

        prompt.append("ANALYSIS PERIOD: ").append(data.period())
                .append(" (").append(data.startDate()).append(" to ").append(data.endDate()).append(")\n\n");

        BigDecimal netBalance = data.totalIncome().subtract(data.totalExpense());
        prompt.append("CURRENT PERIOD SUMMARY:\n");
        prompt.append("- Total Income: $").append(formatAmount(data.totalIncome())).append("\n");
        prompt.append("- Total Expense: $").append(formatAmount(data.totalExpense())).append("\n");
        prompt.append("- Net Balance: $").append(formatAmount(netBalance)).append("\n\n");

        if (data.previousPeriodIncome().compareTo(BigDecimal.ZERO) > 0 ||
            data.previousPeriodExpense().compareTo(BigDecimal.ZERO) > 0) {
            BigDecimal incomeChange = calculatePercentageChange(data.previousPeriodIncome(), data.totalIncome());
            BigDecimal expenseChange = calculatePercentageChange(data.previousPeriodExpense(), data.totalExpense());

            prompt.append("PREVIOUS PERIOD COMPARISON:\n");
            prompt.append("- Previous Income: $").append(formatAmount(data.previousPeriodIncome()))
                    .append(" (").append(formatPercentage(incomeChange)).append(")\n");
            prompt.append("- Previous Expense: $").append(formatAmount(data.previousPeriodExpense()))
                    .append(" (").append(formatPercentage(expenseChange)).append(")\n\n");
        }

        if (!data.categorySummaries().isEmpty()) {
            prompt.append("TOP SPENDING CATEGORIES (Current Period):\n");
            data.categorySummaries().stream()
                    .filter(cs -> cs.totalExpense().compareTo(BigDecimal.ZERO) > 0)
                    .sorted((a, b) -> b.totalExpense().compareTo(a.totalExpense()))
                    .limit(10)
                    .forEach(cs -> prompt.append("- ").append(cs.categoryName())
                            .append(": $").append(formatAmount(cs.totalExpense())).append("\n"));
            prompt.append("\n");
        }

        prompt.append("SPENDING PATTERNS BY DAY OF WEEK:\n");
        for (DayOfWeek day : DayOfWeek.values()) {
            BigDecimal amount = data.expensesByDayOfWeek().getOrDefault(day, BigDecimal.ZERO);
            prompt.append("- ").append(day).append(": $").append(formatAmount(amount)).append("\n");
        }
        prompt.append("\n");

        if (!data.transactionCountByCategory().isEmpty()) {
            prompt.append("TRANSACTION FREQUENCY:\n");
            data.transactionCountByCategory().entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(10)
                    .forEach(entry -> prompt.append("- ").append(entry.getKey())
                            .append(": ").append(entry.getValue()).append(" transactions\n"));
            prompt.append("\n");
        }

        if (!data.averageAmountByCategory().isEmpty()) {
            prompt.append("AVERAGE TRANSACTION AMOUNTS:\n");
            data.averageAmountByCategory().entrySet().stream()
                    .sorted((a, b) -> b.getValue().compareTo(a.getValue()))
                    .limit(10)
                    .forEach(entry -> prompt.append("- ").append(entry.getKey())
                            .append(": $").append(formatAmount(entry.getValue())).append(" per transaction\n"));
            prompt.append("\n");
        }

        prompt.append("Provide insights focusing on:\n");
        prompt.append("1. Trends (period-over-period changes)\n");
        prompt.append("2. Patterns (day-of-week, frequency)\n");
        prompt.append("3. Anomalies (unusual spikes)\n");
        prompt.append("4. Comparisons (category vs category)\n");
        prompt.append("5. Recommendations (budget suggestions)\n\n");

        prompt.append("Format: One insight per line, be specific with percentages and amounts.\n");
        prompt.append("Start each insight directly without numbering or bullet points.");

        return prompt.toString();
    }

    private String callGeminiAPI(String prompt) {
        try {
            GenerateContentResponse response = geminiClient.models.generateContent(
                    aiProperties.getModelName(),
                    prompt,
                    null
            );

            String responseText = response.text();
            if (responseText == null || responseText.trim().isEmpty()) {
                throw new AIInsightsException("Gemini API returned empty response");
            }

            return responseText.trim();
        } catch (Exception e) {
            throw new AIInsightsException("Gemini API call failed: " + e.getMessage(), e);
        }
    }

    private List<String> parseInsights(String aiResponse) {
        return Arrays.stream(aiResponse.split("\n"))
                .map(String::trim)
                .filter(line -> !line.isEmpty())
                .filter(line -> !line.matches("^\\d+\\.\\s*$"))
                .map(line -> line.replaceAll("^[\\d\\-*•]+\\.?\\s*", ""))
                .toList();
    }

    private String generateSummary(List<String> insights, String rawResponse) {
        if (insights.isEmpty()) {
            return "No significant patterns detected";
        }

        if (insights.size() == 1) {
            return insights.get(0);
        }

        String firstInsight = insights.get(0);
        if (firstInsight.length() > 300) {
            return firstInsight.substring(0, 297) + "...";
        }

        return firstInsight;
    }

    private String formatAmount(BigDecimal amount) {
        return amount.setScale(2, RoundingMode.HALF_UP).toString();
    }

    private BigDecimal calculatePercentageChange(BigDecimal previous, BigDecimal current) {
        if (previous.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal change = current.subtract(previous);
        return change.divide(previous, 4, RoundingMode.HALF_UP)
                .multiply(BigDecimal.valueOf(100))
                .setScale(1, RoundingMode.HALF_UP);
    }

    private String formatPercentage(BigDecimal percentage) {
        if (percentage.compareTo(BigDecimal.ZERO) > 0) {
            return "+" + percentage + "%";
        } else if (percentage.compareTo(BigDecimal.ZERO) < 0) {
            return percentage + "%";
        } else {
            return "0%";
        }
    }
}