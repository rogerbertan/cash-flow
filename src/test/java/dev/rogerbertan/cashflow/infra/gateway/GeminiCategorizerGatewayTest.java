package dev.rogerbertan.cashflow.infra.gateway;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

import dev.rogerbertan.cashflow.application.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.entities.Category;
import dev.rogerbertan.cashflow.domain.enums.Type;
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
        when(aiProperties.isEnabled()).thenReturn(false);
        when(aiProperties.getApiKey()).thenReturn("test-api-key");

        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);

        CategorySuggestion result = gateway.suggestCategory("grocery shopping", Type.EXPENSE);

        assertThat(result).isNotNull();
        assertThat(result.category()).isNull();
        assertThat(result.confidence()).isEqualTo("disabled");
        assertThat(result.rawAiResponse()).isEqualTo("AI categorization is disabled");

        verify(aiProperties, times(1)).isEnabled();
        verifyNoInteractions(categoryGateway);
    }

    @Test
    void suggestCategory_ShouldReturnLowConfidenceSuggestion_WhenNoCategoriesAvailable() {
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        when(categoryGateway.findAllCategories()).thenReturn(Collections.emptyList());

        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);

        CategorySuggestion result = gateway.suggestCategory("grocery shopping", Type.EXPENSE);

        assertThat(result).isNotNull();
        assertThat(result.category()).isNull();
        assertThat(result.confidence()).isEqualTo("low");
        assertThat(result.rawAiResponse()).isEqualTo("No categories available in the system");

        verify(aiProperties, times(1)).isEnabled();
        verify(categoryGateway, times(1)).findAllCategories();
    }

    @Test
    void suggestCategory_ShouldThrowException_WhenNoCategoriesMatchType() {
        Category incomeCategory = TestDataFactory.createIncomeCategory();
        when(aiProperties.isEnabled()).thenReturn(true);
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        when(categoryGateway.findAllCategories()).thenReturn(List.of(incomeCategory));

        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);

        assertThatThrownBy(() -> gateway.suggestCategory("grocery shopping", Type.EXPENSE))
                .isInstanceOf(AICategorizeException.class)
                .hasMessageContaining("No categories found for type: EXPENSE");

        verify(aiProperties, times(1)).isEnabled();
        verify(categoryGateway, times(1)).findAllCategories();
    }

    @Test
    void suggestCategory_ShouldThrowException_WhenAPIKeyIsNull() {
        when(aiProperties.getApiKey()).thenReturn(null);

        assertThatThrownBy(() -> new GeminiCategorizerGateway(categoryGateway, aiProperties))
                .isInstanceOf(AICategorizeException.class)
                .hasMessageContaining("Failed to initialize Gemini client");

        verify(aiProperties, atLeastOnce()).getApiKey();
    }

    @Test
    void suggestCategory_ShouldThrowException_WhenAPIKeyIsEmpty() {
        when(aiProperties.getApiKey()).thenReturn("");

        assertThatThrownBy(() -> new GeminiCategorizerGateway(categoryGateway, aiProperties))
                .isInstanceOf(AICategorizeException.class)
                .hasMessageContaining("Failed to initialize Gemini client");

        verify(aiProperties, atLeastOnce()).getApiKey();
    }

    // buildPrompt tests

    @Test
    void buildPrompt_ShouldReturnPromptContainingCategoryNames_WhenCategoriesMatchType() {
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);
        Category expenseCategory = TestDataFactory.createExpenseCategory();

        String result =
                gateway.buildPrompt("grocery shopping", Type.EXPENSE, List.of(expenseCategory));

        assertThat(result)
                .contains(expenseCategory.name())
                .contains("grocery shopping")
                .contains("EXPENSE");
    }

    @Test
    void buildPrompt_ShouldThrowException_WhenNoCategoriesMatchType() {
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);
        Category incomeCategory = TestDataFactory.createIncomeCategory();

        List<Category> categories = List.of(incomeCategory);
        assertThatThrownBy(() -> gateway.buildPrompt("grocery shopping", Type.EXPENSE, categories))
                .isInstanceOf(AICategorizeException.class)
                .hasMessageContaining("No categories found for type: EXPENSE");
    }

    // matchCategory tests

    @Test
    void matchCategory_ShouldReturnCategory_WhenAiResponseMatchesCategoryName() {
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);
        Category expenseCategory = TestDataFactory.createExpenseCategory();

        Category result = gateway.matchCategory("Food", List.of(expenseCategory), Type.EXPENSE);

        assertThat(result).isEqualTo(expenseCategory);
    }

    @Test
    void matchCategory_ShouldReturnNull_WhenAiResponseDoesNotMatchAnyCategory() {
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);
        Category expenseCategory = TestDataFactory.createExpenseCategory();

        Category result =
                gateway.matchCategory("UnknownCategory", List.of(expenseCategory), Type.EXPENSE);

        assertThat(result).isNull();
    }

    @Test
    void matchCategory_ShouldBeCaseInsensitive_WhenComparingCategoryNames() {
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);
        Category expenseCategory = TestDataFactory.createExpenseCategory();

        Category result = gateway.matchCategory("food", List.of(expenseCategory), Type.EXPENSE);

        assertThat(result).isEqualTo(expenseCategory);
    }

    @Test
    void matchCategory_ShouldFilterByType_WhenCategoriesHaveDifferentTypes() {
        when(aiProperties.getApiKey()).thenReturn("test-api-key");
        GeminiCategorizerGateway gateway =
                new GeminiCategorizerGateway(categoryGateway, aiProperties);
        Category incomeCategory = TestDataFactory.createIncomeCategory();
        Category expenseCategory = TestDataFactory.createExpenseCategory();

        Category result =
                gateway.matchCategory(
                        "Salary", List.of(incomeCategory, expenseCategory), Type.EXPENSE);

        assertThat(result).isNull();
    }
}
