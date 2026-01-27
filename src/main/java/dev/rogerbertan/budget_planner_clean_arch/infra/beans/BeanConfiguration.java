package dev.rogerbertan.budget_planner_clean_arch.infra.beans;

import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.CategoryGateway;
import dev.rogerbertan.budget_planner_clean_arch.domain.gateway.TransactionGateway;
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
}
