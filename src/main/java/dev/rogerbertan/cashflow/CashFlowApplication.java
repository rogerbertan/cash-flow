package dev.rogerbertan.cashflow;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public final class CashFlowApplication {

    private CashFlowApplication() {}

    public static void main(String[] args) {
        SpringApplication.run(CashFlowApplication.class, args);
    }
}
