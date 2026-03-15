package dev.rogerbertan.cashflow.application.gateway;

import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySuggestion;

public interface AICategorizerGateway {

    CategorySuggestion suggestCategory(String description, Type type);
}
