package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.valueobjects.CategorySummary;
import dev.rogerbertan.cashflow.infra.dto.CategoriesSummaryResponse;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class CategorySummaryResponseMapper {

    public CategoriesSummaryResponse toDTO(CategorySummary summary) {
        return new CategoriesSummaryResponse(
                summary.categoryName(), summary.totalIncome(), summary.totalExpense());
    }

    public List<CategoriesSummaryResponse> toListDTO(List<CategorySummary> summaries) {
        return summaries.stream().map(this::toDTO).toList();
    }
}
