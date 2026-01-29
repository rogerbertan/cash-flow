package dev.rogerbertan.cash_flow.domain.gateway;

import dev.rogerbertan.cash_flow.domain.enums.Type;
import dev.rogerbertan.cash_flow.domain.valueobjects.CategorySuggestion;

public interface AICategorizerGateway {

    CategorySuggestion suggestCategory(String description, Type type);
}