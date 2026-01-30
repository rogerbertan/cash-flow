package dev.rogerbertan.cash_flow.infra.beans;

import dev.rogerbertan.cash_flow.domain.gateway.AICategorizerGateway;
import dev.rogerbertan.cash_flow.domain.gateway.AIInsightsGateway;
import dev.rogerbertan.cash_flow.domain.gateway.CategoryGateway;
import dev.rogerbertan.cash_flow.domain.gateway.TransactionGateway;
import dev.rogerbertan.cash_flow.domain.usecases.category.*;
import dev.rogerbertan.cash_flow.domain.usecases.insights.GenerateSpendingInsightsUseCase;
import dev.rogerbertan.cash_flow.domain.usecases.summary.GetBalanceUseCase;
import dev.rogerbertan.cash_flow.domain.usecases.summary.GetCategoriesSummaryUseCase;
import dev.rogerbertan.cash_flow.domain.usecases.summary.GetMonthlySummaryUseCase;
import dev.rogerbertan.cash_flow.domain.usecases.transaction.*;
import dev.rogerbertan.cash_flow.infra.config.AIProperties;
import dev.rogerbertan.cash_flow.infra.gateway.CategoryRepositoryGateway;
import dev.rogerbertan.cash_flow.infra.gateway.GeminiCategorizerGateway;
import dev.rogerbertan.cash_flow.infra.gateway.GeminiInsightsGateway;
import dev.rogerbertan.cash_flow.infra.gateway.TransactionRepositoryGateway;
import dev.rogerbertan.cash_flow.infra.mapper.CategoryEntityMapper;
import dev.rogerbertan.cash_flow.infra.mapper.TransactionEntityMapper;
import dev.rogerbertan.cash_flow.infra.persistence.CategoryRepository;
import dev.rogerbertan.cash_flow.infra.persistence.TransactionRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public CategoryGateway categoryGateway(
            CategoryRepository categoryRepository,
            CategoryEntityMapper mapper
    ) {
        return new CategoryRepositoryGateway(categoryRepository, mapper);
    }

    @Bean
    public TransactionGateway transactionGateway(
            TransactionRepository transactionRepository,
            CategoryRepository categoryRepository,
            TransactionEntityMapper mapper
    ) {
        return new TransactionRepositoryGateway(transactionRepository, categoryRepository, mapper);
    }

    @Bean
    public AICategorizerGateway aiCategorizerGateway(
            CategoryGateway categoryGateway,
            AIProperties aiProperties
    ) {
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
    public CreateTransactionUseCase createTransactionUseCase(TransactionGateway transactionGateway) {
        return new CreateTransactionUseCase(transactionGateway);
    }

    @Bean
    public FindAllTransactionUseCase findAllTransactionUseCase(TransactionGateway transactionGateway) {
        return new FindAllTransactionUseCase(transactionGateway);
    }

    @Bean
    public FindTransactionByIdUseCase findTransactionByIdUseCase(TransactionGateway transactionGateway) {
        return new FindTransactionByIdUseCase(transactionGateway);
    }

    @Bean
    public UpdateTransactionUseCase updateTransactionUseCase(TransactionGateway transactionGateway) {
        return new UpdateTransactionUseCase(transactionGateway);
    }

    @Bean
    public DeleteTransactionUseCase deleteTransactionUseCase(TransactionGateway transactionGateway) {
        return new DeleteTransactionUseCase(transactionGateway);
    }

    @Bean
    public GetBalanceUseCase getBalanceUseCase(TransactionGateway transactionGateway) {
        return new GetBalanceUseCase(transactionGateway);
    }

    @Bean
    public GetMonthlySummaryUseCase getMonthlySummaryUseCase(TransactionGateway transactionGateway) {
        return new GetMonthlySummaryUseCase(transactionGateway);
    }

    @Bean
    public GetCategoriesSummaryUseCase getCategoriesSummaryUseCase(TransactionGateway transactionGateway) {
        return new GetCategoriesSummaryUseCase(transactionGateway);
    }

    @Bean
    public SuggestTransactionCategoryUseCase suggestTransactionCategoryUseCase(
            AICategorizerGateway aiCategorizerGateway,
            CategoryGateway categoryGateway
    ) {
        return new SuggestTransactionCategoryUseCase(aiCategorizerGateway, categoryGateway);
    }

    @Bean
    public AIInsightsGateway aiInsightsGateway(AIProperties aiProperties) {
        return new GeminiInsightsGateway(aiProperties);
    }

    @Bean
    public GenerateSpendingInsightsUseCase generateSpendingInsightsUseCase(
            AIInsightsGateway aiInsightsGateway,
            TransactionGateway transactionGateway
    ) {
        return new GenerateSpendingInsightsUseCase(aiInsightsGateway, transactionGateway);
    }
}
