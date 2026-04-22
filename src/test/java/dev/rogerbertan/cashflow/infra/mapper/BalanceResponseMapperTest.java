package dev.rogerbertan.cashflow.infra.mapper;

import static org.assertj.core.api.Assertions.assertThat;

import dev.rogerbertan.cashflow.application.usecases.TestDataFactory;
import dev.rogerbertan.cashflow.domain.valueobjects.Balance;
import dev.rogerbertan.cashflow.infra.dto.BalanceResponse;
import org.junit.jupiter.api.Test;

class BalanceResponseMapperTest {

    private final BalanceResponseMapper mapper = new BalanceResponseMapper();

    @Test
    void toDTO_ShouldMapBalanceValue_WhenCalled() {
        Balance balance = TestDataFactory.createPositiveBalance();

        BalanceResponse result = mapper.toDTO(balance);

        assertThat(result.balance()).isEqualTo(balance.balance());
    }
}
