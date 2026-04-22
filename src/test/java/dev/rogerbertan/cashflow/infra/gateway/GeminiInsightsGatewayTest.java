package dev.rogerbertan.cashflow.infra.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.rogerbertan.cashflow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cashflow.domain.valueobjects.TransactionAnalysisData;
import dev.rogerbertan.cashflow.infra.config.AIProperties;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.Collections;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class GeminiInsightsGatewayTest {

    private AIProperties aiProperties;
    private TransactionAnalysisData sampleData;

    @BeforeEach
    void setUp() {
        aiProperties = mock(AIProperties.class);
        when(aiProperties.getModelName()).thenReturn("gemini-2.5-flash-lite");
        when(aiProperties.isEnabled()).thenReturn(false);

        Map<DayOfWeek, BigDecimal> dayOfWeekMap = new EnumMap<>(DayOfWeek.class);
        dayOfWeekMap.put(DayOfWeek.MONDAY, new BigDecimal("100.00"));

        sampleData =
                new TransactionAnalysisData(
                        LocalDate.now().minusDays(30),
                        LocalDate.now(),
                        "monthly",
                        new BigDecimal("5000.00"),
                        new BigDecimal("3000.00"),
                        new BigDecimal("4800.00"),
                        new BigDecimal("2800.00"),
                        List.of(),
                        List.of(),
                        dayOfWeekMap,
                        new HashMap<>(),
                        new HashMap<>());
    }

    @Test
    void generateInsights_ShouldReturnDisabledMessage_WhenAIDisabled() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        SpendingInsights result = gateway.generateInsights(sampleData);

        assertThat(result).isNotNull();
        assertThat(result.period()).isEqualTo("monthly");
        assertThat(result.insights().getFirst()).contains("disabled");
    }

    @Test
    void generateInsights_ShouldReturnDisabledMessage_WhenApiKeyMissing() {
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn(null);

        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);
        SpendingInsights result = gateway.generateInsights(sampleData);

        assertThat(result).isNotNull();
        assertThat(result.insights().getFirst()).contains("disabled");
    }

    @Test
    void generateInsights_ShouldReturnDisabledMessage_WhenApiKeyEmpty() {
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("");

        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);
        SpendingInsights result = gateway.generateInsights(sampleData);

        assertThat(result).isNotNull();
        assertThat(result.insights().getFirst()).contains("disabled");
    }

    // parseInsights tests

    @Test
    void parseInsights_ShouldReturnNonEmptyLines_WhenResponseHasMultipleLines() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);
        String response = "Insight one\nInsight two\n\nInsight three";

        List<String> result = gateway.parseInsights(response);

        assertThat(result).hasSize(3)
                        .containsExactly("Insight one", "Insight two", "Insight three");
    }

    @Test
    void parseInsights_ShouldFilterEmptyLines_WhenResponseHasBlankLines() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);
        String response = "\n\nOnly line\n\n";

        List<String> result = gateway.parseInsights(response);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo("Only line");
    }

    @Test
    void parseInsights_ShouldStripNumberingPrefixes_WhenResponseHasNumberedLines() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);
        String response = "1. First insight\n2. Second insight";

        List<String> result = gateway.parseInsights(response);

        assertThat(result).hasSize(2);
        assertThat(result.get(0)).isEqualTo("First insight");
        assertThat(result.get(1)).isEqualTo("Second insight");
    }

    @Test
    void parseInsights_ShouldFilterStandaloneNumberLines_WhenResponseHasNumberOnlyLines() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);
        String response = "1.\nActual insight";

        List<String> result = gateway.parseInsights(response);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst()).isEqualTo("Actual insight");
    }

    // generateSummary tests

    @Test
    void generateSummary_ShouldReturnNoPatterns_WhenInsightsListIsEmpty() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        String result = gateway.generateSummary(Collections.emptyList());

        assertThat(result).isEqualTo("No significant patterns detected");
    }

    @Test
    void generateSummary_ShouldReturnSingleInsight_WhenOnlyOneInsightExists() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        String result = gateway.generateSummary(List.of("Only one insight"));

        assertThat(result).isEqualTo("Only one insight");
    }

    @Test
    void generateSummary_ShouldReturnFirstInsight_WhenMultipleInsightsExist() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        String result = gateway.generateSummary(List.of("First insight", "Second insight"));

        assertThat(result).isEqualTo("First insight");
    }

    @Test
    void generateSummary_ShouldTruncateLongInsight_WhenFirstInsightExceeds300Chars() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);
        String longInsight = "a".repeat(400);

        String result = gateway.generateSummary(List.of(longInsight, "Second insight"));

        assertThat(result).hasSize(300)
                        .endsWith("...");
    }

    // formatAmount tests

    @Test
    void formatAmount_ShouldFormatToTwoDecimalPlaces_WhenAmountHasMoreDecimals() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        String result = gateway.formatAmount(new BigDecimal("1234.567"));

        assertThat(result).isEqualTo("1234.57");
    }

    @Test
    void formatAmount_ShouldPadWithZeros_WhenAmountHasFewerDecimals() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        String result = gateway.formatAmount(new BigDecimal("100"));

        assertThat(result).isEqualTo("100.00");
    }

    // calculatePercentageChange tests

    @Test
    void calculatePercentageChange_ShouldReturnZero_WhenPreviousIsZero() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        BigDecimal result =
                gateway.calculatePercentageChange(BigDecimal.ZERO, new BigDecimal("100"));

        assertThat(result).isEqualByComparingTo(BigDecimal.ZERO);
    }

    @Test
    void calculatePercentageChange_ShouldReturnPositivePercent_WhenCurrentIsHigherThanPrevious() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        BigDecimal result =
                gateway.calculatePercentageChange(new BigDecimal("100"), new BigDecimal("150"));

        assertThat(result).isEqualByComparingTo(new BigDecimal("50.0"));
    }

    @Test
    void calculatePercentageChange_ShouldReturnNegativePercent_WhenCurrentIsLowerThanPrevious() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        BigDecimal result =
                gateway.calculatePercentageChange(new BigDecimal("100"), new BigDecimal("50"));

        assertThat(result).isEqualByComparingTo(new BigDecimal("-50.0"));
    }

    // formatPercentage tests

    @Test
    void formatPercentage_ShouldReturnWithPlusSign_WhenPercentageIsPositive() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        String result = gateway.formatPercentage(new BigDecimal("25.5"));

        assertThat(result).isEqualTo("+25.5%");
    }

    @Test
    void formatPercentage_ShouldReturnWithMinusSign_WhenPercentageIsNegative() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        String result = gateway.formatPercentage(new BigDecimal("-10.0"));

        assertThat(result).isEqualTo("-10.0%");
    }

    @Test
    void formatPercentage_ShouldReturnZeroString_WhenPercentageIsZero() {
        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);

        String result = gateway.formatPercentage(BigDecimal.ZERO);

        assertThat(result).isEqualTo("0%");
    }
}
