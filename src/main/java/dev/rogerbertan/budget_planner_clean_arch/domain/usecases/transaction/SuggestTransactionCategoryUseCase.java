package dev.rogerbertan.budget_planner_clean_arch.domain.usecases.transaction;

import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.AICategorizerGateway;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;
import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.CategorySuggestion;

public class SuggestTransactionCategoryUseCase {

    private final AICategorizerGateway aiCategorizerGateway;
    private final CategoryGateway categoryGateway;

    public SuggestTransactionCategoryUseCase(
            AICategorizerGateway aiCategorizerGateway,
            CategoryGateway categoryGateway
    ) {
        this.aiCategorizerGateway = aiCategorizerGateway;
        this.categoryGateway = categoryGateway;
    }

    public CategorySuggestion execute(String description, Type type) {
        return aiCategorizerGateway.suggestCategory(description, type);
    }
}