package dev.rogerbertan.cashflow.infra.gateway;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import dev.rogerbertan.cashflow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cashflow.domain.valueobjects.TransactionAnalysisData;
import dev.rogerbertan.cashflow.infra.config.AIProperties;
import java.math.BigDecimal;
import java.time.DayOfWeek;
import java.time.LocalDate;
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

        Map<DayOfWeek, BigDecimal> dayOfWeekMap = new HashMap<>();
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

        assertNotNull(result);
        assertEquals("monthly", result.period());
        assertTrue(result.insights().get(0).contains("disabled"));
    }

    @Test
    void generateInsights_ShouldReturnDisabledMessage_WhenApiKeyMissing() {
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn(null);

        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);
        SpendingInsights result = gateway.generateInsights(sampleData);

        assertNotNull(result);
        assertTrue(result.insights().get(0).contains("disabled"));
    }

    @Test
    void generateInsights_ShouldReturnDisabledMessage_WhenApiKeyEmpty() {
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("");

        GeminiInsightsGateway gateway = new GeminiInsightsGateway(aiProperties);
        SpendingInsights result = gateway.generateInsights(sampleData);

        assertNotNull(result);
        assertTrue(result.insights().get(0).contains("disabled"));
    }
}
