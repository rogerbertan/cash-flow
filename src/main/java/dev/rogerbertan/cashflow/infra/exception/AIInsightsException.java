package dev.rogerbertan.cashflow.infra.exception;

import org.springframework.http.HttpStatus;

public class AIInsightsException extends BudgetPlannerException {

    public AIInsightsException(String error) {
        super(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public AIInsightsException(String error, Throwable cause) {
        super(error, cause, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
