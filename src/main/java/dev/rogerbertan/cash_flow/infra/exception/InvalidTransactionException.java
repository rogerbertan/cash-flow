package dev.rogerbertan.cash_flow.infra.exception;

import org.springframework.http.HttpStatus;

public class InvalidTransactionException extends BudgetPlannerException {

    public InvalidTransactionException(String error) {
        super(error, HttpStatus.BAD_REQUEST);
    }

    public InvalidTransactionException(String error, Throwable cause) {
        super(error, cause, HttpStatus.BAD_REQUEST);
    }
}