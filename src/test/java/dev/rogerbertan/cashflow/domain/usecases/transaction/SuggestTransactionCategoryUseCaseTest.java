package dev.rogerbertan.cashflow.domain.usecases.transaction;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.AICategorizerGateway;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.domain.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySuggestion;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class SuggestTransactionCategoryUseCaseTest {

    @Mock private AICategorizerGateway aiCategorizerGateway;

    @Mock private CategoryGateway categoryGateway;

    @InjectMocks private SuggestTransactionCategoryUseCase useCase;

    @Test
    void execute_ShouldReturnCategorySuggestion_WhenValidParametersProvided() {
        // Arrange
        String description = "grocery shopping";
        Type type = Type.EXPENSE;
        CategorySuggestion expectedSuggestion = TestDataFactory.createHighConfidenceSuggestion();

        when(aiCategorizerGateway.suggestCategory(description, type))
                .thenReturn(expectedSuggestion);

        // Act
        CategorySuggestion result = useCase.execute(description, type);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEqualTo(expectedSuggestion);
        assertThat(result.category()).isNotNull();
        assertThat(result.category().name()).isEqualTo("Food");
        assertThat(result.confidence()).isEqualTo("high");
        assertThat(result.rawAiResponse()).isEqualTo("Food");
    }

    @Test
    void execute_ShouldDelegateToAIGateway_WhenCalled() {
        // Arrange
        String description = "monthly salary payment";
        Type type = Type.INCOME;
        CategorySuggestion suggestion = TestDataFactory.createHighConfidenceSuggestion();

        when(aiCategorizerGateway.suggestCategory(description, type)).thenReturn(suggestion);

        // Act
        useCase.execute(description, type);

        // Assert
        ArgumentCaptor<String> descriptionCaptor = ArgumentCaptor.forClass(String.class);
        ArgumentCaptor<Type> typeCaptor = ArgumentCaptor.forClass(Type.class);

        verify(aiCategorizerGateway, times(1))
                .suggestCategory(descriptionCaptor.capture(), typeCaptor.capture());

        assertThat(descriptionCaptor.getValue()).isEqualTo(description);
        assertThat(typeCaptor.getValue()).isEqualTo(type);

        verifyNoMoreInteractions(aiCategorizerGateway);
        verifyNoInteractions(categoryGateway);
    }
}
