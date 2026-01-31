package dev.rogerbertan.cashflow.infra.exception;

import org.springframework.http.HttpStatus;

public class AICategorizeException extends BudgetPlannerException {

    public AICategorizeException(String error) {
        super(error, HttpStatus.SERVICE_UNAVAILABLE);
    }

    public AICategorizeException(String error, Throwable cause) {
        super(error, cause, HttpStatus.SERVICE_UNAVAILABLE);
    }
}
