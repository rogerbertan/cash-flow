package dev.rogerbertan.budget_planner_clean_arch.infra.mapper;

import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.CategorySummary;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.CategoriesSummaryResponse;
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