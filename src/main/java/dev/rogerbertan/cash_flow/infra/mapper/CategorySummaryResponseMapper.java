package dev.rogerbertan.cash_flow.infra.mapper;

import dev.rogerbertan.cash_flow.domain.valueobjects.CategorySummary;
import dev.rogerbertan.cash_flow.infra.dto.CategoriesSummaryResponse;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class CategorySummaryResponseMapper {

    public CategoriesSummaryResponse toDTO(CategorySummary summary) {
        return new CategoriesSummaryResponse(
                summary.categoryName(),
                summary.totalIncome(),
                summary.totalExpense()
        );
    }

    public List<CategoriesSummaryResponse> toListDTO(List<CategorySummary> summaries) {
        return summaries.stream()
                .map(this::toDTO)
                .toList();
    }
}