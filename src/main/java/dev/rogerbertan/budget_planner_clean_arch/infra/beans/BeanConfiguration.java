package dev.rogerbertan.budget_planner_clean_arch.infra.beans;

import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;
import dev.rogerbertan.budget_planner_clean_arch.domain.usecases.*;
import dev.rogerbertan.budget_planner_clean_arch.infra.gateway.CategoryRepositoryGateway;
import dev.rogerbertan.budget_planner_clean_arch.infra.gateway.TransactionRepositoryGateway;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.CategoryEntityMapper;
import dev.rogerbertan.budget_planner_clean_arch.infra.mapper.TransactionEntityMapper;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.CategoryRepository;
import dev.rogerbertan.budget_planner_clean_arch.infra.persistence.TransactionRepository;
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
}
