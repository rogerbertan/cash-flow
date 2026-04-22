package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySuggestion;
import dev.rogerbertan.cashflow.infra.dto.CategoryResponse;
import dev.rogerbertan.cashflow.infra.dto.CategorySuggestionResponse;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class CategorySuggestionMapperTest {

    @Mock private CategoryResponseMapper categoryResponseMapper;

    @InjectMocks private CategorySuggestionMapper mapper;

    @Test
    void toDTO_ShouldReturnNullCategoryAndNoMatchMessage_WhenSuggestionCategoryIsNull() {
        CategorySuggestion suggestion = TestDataFactory.createLowConfidenceSuggestion();

        CategorySuggestionResponse result = mapper.toDTO(suggestion);

        assertThat(result.suggestedCategory()).isNull();
        assertThat(result.confidence()).isEqualTo("low");
        assertThat(result.message()).isEqualTo("No matching category found for this transaction");
    }

    @Test
    void toDTO_ShouldReturnMappedCategoryAndSuccessMessage_WhenSuggestionCategoryIsNotNull() {
        CategorySuggestion suggestion = TestDataFactory.createHighConfidenceSuggestion();
        CategoryResponse expectedResponse = TestDataFactory.createExpenseCategoryResponse();
        when(categoryResponseMapper.toDTO(suggestion.category())).thenReturn(expectedResponse);

        CategorySuggestionResponse result = mapper.toDTO(suggestion);

        assertThat(result.suggestedCategory()).isEqualTo(expectedResponse);
        assertThat(result.confidence()).isEqualTo("high");
        assertThat(result.message()).isEqualTo("Category suggestion successful");
    }

    @Test
    void toDTO_ShouldDelegateToResponseMapper_WhenCategoryIsPresent() {
        CategorySuggestion suggestion = TestDataFactory.createHighConfidenceSuggestion();
        when(categoryResponseMapper.toDTO(suggestion.category()))
                .thenReturn(TestDataFactory.createExpenseCategoryResponse());

        mapper.toDTO(suggestion);

        verify(categoryResponseMapper).toDTO(suggestion.category());
    }
}
