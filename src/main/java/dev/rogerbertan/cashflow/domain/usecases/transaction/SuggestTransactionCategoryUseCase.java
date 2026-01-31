package dev.rogerbertan.cashflow.domain.usecases.transaction;

import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.AICategorizerGateway;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySuggestion;

public class SuggestTransactionCategoryUseCase {

    private final AICategorizerGateway aiCategorizerGateway;
    private final CategoryGateway categoryGateway;

    public SuggestTransactionCategoryUseCase(
            AICategorizerGateway aiCategorizerGateway, CategoryGateway categoryGateway) {
        this.aiCategorizerGateway = aiCategorizerGateway;
        this.categoryGateway = categoryGateway;
    }

    public CategorySuggestion execute(String description, Type type) {
        return aiCategorizerGateway.suggestCategory(description, type);
    }
}
