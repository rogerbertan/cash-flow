package dev.rogerbertan.cashflow.infra.beans;

import dev.rogerbertan.cashflow.application.gateway.AICategorizerGateway;
import dev.rogerbertan.cashflow.application.gateway.AIInsightsGateway;
import dev.rogerbertan.cashflow.application.gateway.CategoryGateway;
import dev.rogerbertan.cashflow.application.gateway.TransactionGateway;
import dev.rogerbertan.cashflow.application.usecases.category.CreateCategoryUseCase;
import dev.rogerbertan.cashflow.application.usecases.category.DeleteCategoryUseCase;
import dev.rogerbertan.cashflow.application.usecases.category.FindAllCategoriesUseCase;
import dev.rogerbertan.cashflow.application.usecases.category.FindCategoryByIdUseCase;
import dev.rogerbertan.cashflow.application.usecases.category.UpdateCategoryUseCase;
import dev.rogerbertan.cashflow.application.usecases.insights.GenerateSpendingInsightsUseCase;
import dev.rogerbertan.cashflow.application.usecases.summary.GetBalanceUseCase;
import dev.rogerbertan.cashflow.application.usecases.summary.GetCategoriesSummaryUseCase;
import dev.rogerbertan.cashflow.application.usecases.summary.GetMonthlySummaryUseCase;
import dev.rogerbertan.cashflow.application.usecases.transaction.CreateTransactionUseCase;
import dev.rogerbertan.cashflow.application.usecases.transaction.DeleteTransactionUseCase;
import dev.rogerbertan.cashflow.application.usecases.transaction.FindAllTransactionUseCase;
import dev.rogerbertan.cashflow.application.usecases.transaction.FindTransactionByIdUseCase;
import dev.rogerbertan.cashflow.application.usecases.transaction.SuggestTransactionCategoryUseCase;
import dev.rogerbertan.cashflow.application.usecases.transaction.UpdateTransactionUseCase;
import dev.rogerbertan.cashflow.infra.config.AIProperties;
import dev.rogerbertan.cashflow.infra.gateway.CategoryRepositoryGateway;
import dev.rogerbertan.cashflow.infra.gateway.GeminiCategorizerGateway;
import dev.rogerbertan.cashflow.infra.gateway.GeminiInsightsGateway;
import dev.rogerbertan.cashflow.infra.gateway.TransactionRepositoryGateway;
import dev.rogerbertan.cashflow.infra.mapper.CategoryEntityMapper;
import dev.rogerbertan.cashflow.infra.mapper.TransactionEntityMapper;
import dev.rogerbertan.cashflow.infra.persistence.CategoryRepository;
import dev.rogerbertan.cashflow.infra.persistence.TransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CategoryGateway categoryGateway(
            CategoryRepository categoryRepository, CategoryEntityMapper mapper) {
        return new CategoryRepositoryGateway(categoryRepository, mapper);
    }

    @Bean
    public TransactionGateway transactionGateway(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            TransactionEntityMapper mapper) {
        return new TransactionRepositoryGateway(transactionRepository, categoryRepository, mapper);
    }

    @Bean
    public AICategorizerGateway aiCategorizerGateway(
            CategoryGateway categoryGateway, AIProperties aiProperties) {
        return new GeminiCategorizerGateway(categoryGateway, aiProperties);
    }

    @Bean
    public CreateCategoryUseCase createCategoryUseCase(CategoryGateway categoryGateway) {
        return new CreateCategoryUseCase(categoryGateway);
    }

    @Bean
    public FindAllCategoriesUseCase findAllCategoriesUseCase(CategoryGateway categoryGateway) {
        return new FindAllCategoriesUseCase(categoryGateway);
    }

    @Bean
    public FindCategoryByIdUseCase findCategoryByIdUseCase(CategoryGateway categoryGateway) {
        return new FindCategoryByIdUseCase(categoryGateway);
    }

    @Bean
    public UpdateCategoryUseCase updateCategoryUseCase(CategoryGateway categoryGateway) {
        return new UpdateCategoryUseCase(categoryGateway);
    }

    @Bean
    public DeleteCategoryUseCase deleteCategoryUseCase(CategoryGateway categoryGateway) {
        return new DeleteCategoryUseCase(categoryGateway);
    }

    @Bean
    public CreateTransactionUseCase createTransactionUseCase(
            TransactionGateway transactionGateway) {
        return new CreateTransactionUseCase(transactionGateway);
    }

    @Bean
    public FindAllTransactionUseCase findAllTransactionUseCase(
            TransactionGateway transactionGateway) {
        return new FindAllTransactionUseCase(transactionGateway);
    }

    @Bean
    public FindTransactionByIdUseCase findTransactionByIdUseCase(
            TransactionGateway transactionGateway) {
        return new FindTransactionByIdUseCase(transactionGateway);
    }

    @Bean
    public UpdateTransactionUseCase updateTransactionUseCase(
            TransactionGateway transactionGateway) {
        return new UpdateTransactionUseCase(transactionGateway);
    }

    @Bean
    public DeleteTransactionUseCase deleteTransactionUseCase(
            TransactionGateway transactionGateway) {
        return new DeleteTransactionUseCase(transactionGateway);
    }

    @Bean
    public GetBalanceUseCase getBalanceUseCase(TransactionGateway transactionGateway) {
        return new GetBalanceUseCase(transactionGateway);
    }

    @Bean
    public GetMonthlySummaryUseCase getMonthlySummaryUseCase(
            TransactionGateway transactionGateway) {
        return new GetMonthlySummaryUseCase(transactionGateway);
    }

    @Bean
    public GetCategoriesSummaryUseCase getCategoriesSummaryUseCase(
            TransactionGateway transactionGateway) {
        return new GetCategoriesSummaryUseCase(transactionGateway);
    }

    @Bean
    public SuggestTransactionCategoryUseCase suggestTransactionCategoryUseCase(
            AICategorizerGateway aiCategorizerGateway, CategoryGateway categoryGateway) {
        return new SuggestTransactionCategoryUseCase(aiCategorizerGateway, categoryGateway);
    }

    @Bean
    public AIInsightsGateway aiInsightsGateway(AIProperties aiProperties) {
        return new GeminiInsightsGateway(aiProperties);
    }

    @Bean
    public GenerateSpendingInsightsUseCase generateSpendingInsightsUseCase(
            AIInsightsGateway aiInsightsGateway, TransactionGateway transactionGateway) {
        return new GenerateSpendingInsightsUseCase(aiInsightsGateway, transactionGateway);
    }
}
