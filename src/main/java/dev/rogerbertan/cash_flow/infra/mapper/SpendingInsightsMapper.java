package dev.rogerbertan.cash_flow.infra.mapper;

import dev.rogerbertan.cash_flow.domain.valueobjects.SpendingInsights;
import dev.rogerbertan.cash_flow.infra.dto.SpendingInsightsResponse;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class SpendingInsightsMapper {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    public SpendingInsightsResponse toDTO(SpendingInsights insights) {
        return new SpendingInsightsResponse(
                insights.insights(),
                insights.period(),
                insights.summary(),
                LocalDateTime.now().format(FORMATTER)
        );
    }
}