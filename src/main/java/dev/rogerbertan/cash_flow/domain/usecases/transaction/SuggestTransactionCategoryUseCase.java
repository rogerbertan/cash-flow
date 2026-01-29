package dev.rogerbertan.cash_flow.domain.usecases.transaction;

import dev.rogerbertan.cash_flow.domain.enums.Type;
import dev.rogerbertan.cash_flow.domain.gateway.AICategorizerGateway;
import dev.rogerbertan.cash_flow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cash_flow.domain.valueobjects.CategorySuggestion;

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