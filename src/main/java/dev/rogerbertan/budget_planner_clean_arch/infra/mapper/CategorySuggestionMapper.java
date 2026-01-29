package dev.rogerbertan.budget_planner_clean_arch.infra.mapper;

import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.CategorySuggestion;
import dev.rogerbertan.budget_planner_clean_arch.infra.dto.CategorySuggestionResponse;
import org.springframework.stereotype.Component;

@Component
public class CategorySuggestionMapper {

    private final CategoryResponseMapper categoryResponseMapper;

    public CategorySuggestionMapper(CategoryResponseMapper categoryResponseMapper) {
        this.categoryResponseMapper = categoryResponseMapper;
    }

    public CategorySuggestionResponse toDTO(CategorySuggestion suggestion) {
        if (suggestion.category() == null) {
            return new CategorySuggestionResponse(
                    null,
                    suggestion.confidence(),
                    "No matching category found for this transaction"
            );
        }

        return new CategorySuggestionResponse(
                categoryResponseMapper.toDTO(suggestion.category()),
                suggestion.confidence(),
                "Category suggestion successful"
        );
    }
}