package dev.rogerbertan.cashflow.infra.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
import dev.rogerbertan.cashflow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.domain.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.valueobjects.CategorySuggestion;
import dev.rogerbertan.cashflow.infra.config.AIProperties;
import dev.rogerbertan.cashflow.infra.exception.AICategorizeException;
import java.util.Collections;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class GeminiCategorizerGatewayTest {

    @Mock private CategoryGateway categoryGateway;

    @Mock private AIProperties aiProperties;

    @Test
    void suggestCategory_ShouldReturnDisabledSuggestion_WhenAIIsDisabled() {
        // Arrange
        when(aiProperties.isEnabled()).thenReturn(false);
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        when(aiProperties.getModelName()).thenReturn("gemini-pro");

        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);

        // Act
        CategorySuggestion result = gateway.suggestCategory("grocery shopping", Type.EXPENSE);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.category()).isNull();
        assertThat(result.confidence()).isEqualTo("disabled");
        assertThat(result.rawAiResponse()).isEqualTo("AI categorization is disabled");

        verify(aiProperties, times(1)).isEnabled();
        verifyNoInteractions(categoryGateway);
    }

    @Test
    void suggestCategory_ShouldReturnLowConfidenceSuggestion_WhenNoCategoriesAvailable() {
        // Arrange
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        when(aiProperties.getModelName()).thenReturn("gemini-pro");
        when(categoryGateway.findAllCategories()).thenReturn(Collections.emptyList());

        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);

        // Act
        CategorySuggestion result = gateway.suggestCategory("grocery shopping", Type.EXPENSE);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.category()).isNull();
        assertThat(result.confidence()).isEqualTo("low");
        assertThat(result.rawAiResponse()).isEqualTo("No categories available in the system");

        verify(aiProperties, times(1)).isEnabled();
        verify(categoryGateway, times(1)).findAllCategories();
    }

    @Test
    void suggestCategory_ShouldThrowException_WhenNoCategoriesMatchType() {
        // Arrange
        Category incomeCategory = TestDataFactory.createIncomeCategory();
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        when(aiProperties.getModelName()).thenReturn("gemini-pro");
        when(categoryGateway.findAllCategories()).thenReturn(List.of(incomeCategory));

        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);

        // Act & Assert
        assertThatThrownBy(() -> gateway.suggestCategory("grocery shopping", Type.EXPENSE))
                .isInstanceOf(AICategorizeException.class)
                .hasMessageContaining("No categories found for type: EXPENSE");

        verify(aiProperties, times(1)).isEnabled();
        verify(categoryGateway, times(1)).findAllCategories();
    }

    @Test
    void suggestCategory_ShouldThrowException_WhenAPIKeyIsNull() {
        // Arrange
        when(aiProperties.getApiKey()).thenReturn(null);
        when(aiProperties.getModelName()).thenReturn("gemini-pro");

        // Act & Assert
        assertThatThrownBy(() -> new GeminiCategorizerGateway(categoryGateway, aiProperties))
                .isInstanceOf(AICategorizeException.class)
                .hasMessageContaining("Failed to initialize Gemini client");

        verify(aiProperties, atLeastOnce()).getApiKey();
    }

    @Test
    void suggestCategory_ShouldThrowException_WhenAPIKeyIsEmpty() {
        // Arrange
        when(aiProperties.getApiKey()).thenReturn("");
        when(aiProperties.getModelName()).thenReturn("gemini-pro");

        // Act & Assert
        assertThatThrownBy(() -> new GeminiCategorizerGateway(categoryGateway, aiProperties))
                .isInstanceOf(AICategorizeException.class)
                .hasMessageContaining("Failed to initialize Gemini client");

        verify(aiProperties, atLeastOnce()).getApiKey();
    }
}
