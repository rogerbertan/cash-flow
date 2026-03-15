package dev.rogerbertan.cashflow.application.usecases.transaction;

import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.application.gateway.AICategorizerGateway;
import dev.rogerbertan.cashflow.application.gateway.CategoryGateway;
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
