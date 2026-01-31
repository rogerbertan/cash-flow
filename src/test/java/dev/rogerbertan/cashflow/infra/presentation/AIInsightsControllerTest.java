package dev.rogerbertan.cashflow.infra.presentation;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.rogerbertan.cashflow.domain.usecases.insights.GenerateSpendingInsightsUseCase;
import dev.rogerbertan.cashflow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cashflow.infra.dto.SpendingInsightsResponse;
import dev.rogerbertan.cashflow.infra.mapper.SpendingInsightsMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class AIInsightsControllerTest {

    @Mock private GenerateSpendingInsightsUseCase generateSpendingInsightsUseCase;

    @Mock private SpendingInsightsMapper spendingInsightsMapper;

    @InjectMocks private AIInsightsController controller;

    private SpendingInsights sampleInsights;
    private SpendingInsightsResponse sampleResponse;

    @BeforeEach
    void setUp() {
        sampleInsights =
                new SpendingInsights(
                        List.of("Your spending increased 20%", "Good saving habits"),
                        "monthly",
                        "Overall positive trends",
                        "raw_response");

        sampleResponse =
                new SpendingInsightsResponse(
                        sampleInsights.insights(),
                        sampleInsights.period(),
                        sampleInsights.summary(),
                        "2026-01-29T12:00:00");
    }

    @Test
    void getSpendingInsights_ShouldReturnInsights_WhenValidPeriodProvided() {
        when(generateSpendingInsightsUseCase.execute("monthly")).thenReturn(sampleInsights);
        when(spendingInsightsMapper.toDTO(any())).thenReturn(sampleResponse);

        ResponseEntity<SpendingInsightsResponse> response =
                controller.getSpendingInsights("monthly");

        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        assertNotNull(response.getBody());
        assertEquals("monthly", response.getBody().period());
        assertEquals(2, response.getBody().insights().size());

        verify(generateSpendingInsightsUseCase).execute("monthly");
        verify(spendingInsightsMapper).toDTO(sampleInsights);
    }

    @Test
    void getSpendingInsights_ShouldUseDefaultPeriod_WhenNoParameterProvided() {
        when(generateSpendingInsightsUseCase.execute("monthly")).thenReturn(sampleInsights);
        when(spendingInsightsMapper.toDTO(any())).thenReturn(sampleResponse);

        ResponseEntity<SpendingInsightsResponse> response =
                controller.getSpendingInsights("monthly");

        assertNotNull(response);
        verify(generateSpendingInsightsUseCase).execute("monthly");
    }

    @Test
    void getSpendingInsights_ShouldHandleWeeklyPeriod() {
        SpendingInsights weeklyInsights =
                new SpendingInsights(List.of("Weekly insight"), "weekly", "Weekly summary", "raw");
        SpendingInsightsResponse weeklyResponse =
                new SpendingInsightsResponse(
                        weeklyInsights.insights(),
                        "weekly",
                        weeklyInsights.summary(),
                        "2026-01-29T12:00:00");

        when(generateSpendingInsightsUseCase.execute("weekly")).thenReturn(weeklyInsights);
        when(spendingInsightsMapper.toDTO(any())).thenReturn(weeklyResponse);

        ResponseEntity<SpendingInsightsResponse> response =
                controller.getSpendingInsights("weekly");

        assertNotNull(response);
        assertEquals("weekly", response.getBody().period());
        verify(generateSpendingInsightsUseCase).execute("weekly");
    }
}
