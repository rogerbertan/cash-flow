package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rogerbertan.cashflow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cashflow.infra.dto.SpendingInsightsResponse;
import java.util.List;
import org.junit.jupiter.api.Test;

class SpendingInsightsMapperTest {

    private final SpendingInsightsMapper mapper = new SpendingInsightsMapper();

    @Test
    void toDTO_ShouldMapInsightsPeriodAndSummary_WhenCalled() {
        SpendingInsights insights =
                new SpendingInsights(
                        List.of("Spend less on food", "Save more"),
                        "monthly",
                        "Overall spending is under control",
                        "raw ai response");

        SpendingInsightsResponse result = mapper.toDTO(insights);

        assertThat(result.insights()).isEqualTo(insights.insights());
        assertThat(result.period()).isEqualTo("monthly");
        assertThat(result.summary()).isEqualTo("Overall spending is under control");
    }

    @Test
    void toDTO_ShouldSetGeneratedAtToCurrentTime_WhenMapping() {
        SpendingInsights insights =
                new SpendingInsights(List.of("tip"), "weekly", "summary", "raw");

        SpendingInsightsResponse result = mapper.toDTO(insights);

        assertThat(result.generatedAt()).isNotNull();
    }

    @Test
    void toDTO_ShouldFormatGeneratedAtAsIsoLocalDateTime_WhenMapping() {
        SpendingInsights insights =
                new SpendingInsights(List.of("tip"), "yearly", "summary", "raw");

        SpendingInsightsResponse result = mapper.toDTO(insights);

        assertThat(result.generatedAt()).matches("\\d{4}-\\d{2}-\\d{2}T\\d{2}:\\d{2}:\\d{2}.*");
    }
}
