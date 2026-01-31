package dev.rogerbertan.cashflow.infra.mapper;

import dev.rogerbertan.cashflow.domain.valueobjects.CategorySuggestion;
import dev.rogerbertan.cashflow.infra.dto.CategorySuggestionResponse;
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
                    "No matching category found for this transaction");
        }

        return new CategorySuggestionResponse(
                categoryResponseMapper.toDTO(suggestion.category()),
                suggestion.confidence(),
                "Category suggestion successful");
    }
}
