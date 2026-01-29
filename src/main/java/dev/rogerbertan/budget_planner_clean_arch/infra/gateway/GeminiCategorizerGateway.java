package dev.rogerbertan.budget_planner_clean_arch.infra.gateway;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;
import dev.rogerbertan.budget_planner_clean_arch.domain.entities.Category;
import dev.rogerbertan.budget_planner_clean_arch.domain.enums.Type;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.AICategorizerGateway;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;
import dev.rogerbertan.budget_planner_clean_arch.domain.valueobjects.CategorySuggestion;
import dev.rogerbertan.budget_planner_clean_arch.infra.config.AIProperties;
import dev.rogerbertan.budget_planner_clean_arch.infra.exception.AICategorizeException;

import java.util.List;
import java.util.stream.Collectors;

public class GeminiCategorizerGateway implements AICategorizerGateway {

    private final CategoryGateway categoryGateway;
    private final AIProperties aiProperties;
    private final Client geminiClient;

    public GeminiCategorizerGateway(
            CategoryGateway categoryGateway,
            AIProperties aiProperties
    ) {
        this.categoryGateway = categoryGateway;
        this.aiProperties = aiProperties;
        this.geminiClient = initializeClient();
    }

    private Client initializeClient() {
        try {
            if (aiProperties.getApiKey() == null || aiProperties.getApiKey().isEmpty()) {
                throw new AICategorizeException("Gemini API key is not configured. Set GOOGLE_API_KEY environment variable.");
            }
            return Client.builder().apiKey(aiProperties.getApiKey()).build();
        } catch (Exception e) {
            throw new AICategorizeException("Failed to initialize Gemini client", e);
        }
    }

    @Override
    public CategorySuggestion suggestCategory(String description, Type type) {
        if (!aiProperties.isEnabled()) {
            return new CategorySuggestion(null, "disabled", "AI categorization is disabled");
        }

        List<Category> allCategories = categoryGateway.findAllCategories();
        if (allCategories.isEmpty()) {
            return new CategorySuggestion(null, "low", "No categories available in the system");
        }

        String prompt = buildPrompt(description, type, allCategories);

        try {
            String aiResponse = callGeminiAPI(prompt);
            Category matchedCategory = matchCategory(aiResponse, allCategories, type);

            if (matchedCategory != null) {
                return new CategorySuggestion(matchedCategory, "high", aiResponse);
            } else {
                return new CategorySuggestion(null, "low", aiResponse);
            }
        } catch (Exception e) {
            throw new AICategorizeException("Failed to get AI category suggestion: " + e.getMessage(), e);
        }
    }

    private String buildPrompt(String description, Type type, List<Category> categories) {
        List<String> categoryNames = categories.stream()
                .filter(category -> category.type() == type)
                .map(Category::name)
                .toList();

        if (categoryNames.isEmpty()) {
            throw new AICategorizeException("No categories found for type: " + type);
        }

        StringBuilder prompt = new StringBuilder();
        prompt.append("You are a transaction categorizer. Select the most appropriate category ");
        prompt.append("from the list below based on the transaction description.\n\n");
        prompt.append("Available ").append(type).append(" categories:\n");

        for (String categoryName : categoryNames) {
            prompt.append("- ").append(categoryName).append("\n");
        }

        prompt.append("\nTransaction: \"").append(description).append("\"\n\n");
        prompt.append("Respond with ONLY the category name, nothing else.");

        return prompt.toString();
    }

    private String callGeminiAPI(String prompt) {
        try {
            GenerateContentResponse response = geminiClient.models.generateContent(
                    aiProperties.getModelName(),
                    prompt,
                    null
            );

            String responseText = response.text();
            if (responseText == null || responseText.trim().isEmpty()) {
                throw new AICategorizeException("Gemini API returned empty response");
            }

            return responseText.trim();
        } catch (Exception e) {
            throw new AICategorizeException("Gemini API call failed: " + e.getMessage(), e);
        }
    }

    private Category matchCategory(String aiResponse, List<Category> categories, Type type) {
        return categories.stream()
                .filter(category -> category.type() == type)
                .filter(category -> category.name().equalsIgnoreCase(aiResponse))
                .findFirst()
                .orElse(null);
    }
}