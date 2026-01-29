package dev.rogerbertan.budget_planner_clean_arch.domain.gateway;

import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;
import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.CategorySuggestion;

public interface AICategorizerGateway {

    CategorySuggestion suggestCategory(String description, Type type);
}