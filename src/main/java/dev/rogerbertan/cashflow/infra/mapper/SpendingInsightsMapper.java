package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cashflow.infra.dto.SpendingInsightsResponse;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import org.springframework.stereotype.Component;

@Component
public class SpendingInsightsMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public SpendingInsightsResponse toDTO(SpendingInsights insights) {
        return new SpendingInsightsResponse(
                insights.insights(),
                insights.period(),
                insights.summary(),
                LocalDateTime.now().format(FORMATTER));
    }
}
