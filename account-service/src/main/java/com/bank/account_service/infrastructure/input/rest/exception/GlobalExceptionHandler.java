package com.bank.account_service.infrastructure.input.rest.exception;

import com.bank.account_service.domain.exceptions.AccountExceptions;
import com.bank.account_service.infrastructure.input.rest.model.ErrorResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.bind.support.WebExchangeBindException;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.OffsetDateTime;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(AccountExceptions.AccountNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleAccountNotFoundException(
            AccountExceptions.AccountNotFoundException ex,
            ServerWebExchange exchange) {

        log.error("Account not found: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(AccountExceptions.InsufficientBalanceException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInsufficientBalanceException(
            AccountExceptions.InsufficientBalanceException ex,
            ServerWebExchange exchange) {

        log.error("Insufficient balance: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(AccountExceptions.InvalidMovementException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidMovementException(
            AccountExceptions.InvalidMovementException ex,
            ServerWebExchange exchange) {

        log.error("Invalid movement: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(AccountExceptions.InactiveAccountException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInactiveAccountException(
            AccountExceptions.InactiveAccountException ex,
            ServerWebExchange exchange) {

        log.error("Inactive account: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(AccountExceptions.InvalidAccountDataException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleInvalidAccountDataException(
            AccountExceptions.InvalidAccountDataException ex,
            ServerWebExchange exchange) {

        log.error("Invalid account data: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(AccountExceptions.MovementNotFoundException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleMovementNotFoundException(
            AccountExceptions.MovementNotFoundException ex,
            ServerWebExchange exchange) {

        log.error("Movement not found: {}", ex.getMessage());

        ErrorResponse error = buildErrorResponse(
                HttpStatus.NOT_FOUND,
                ex.getMessage(),
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.NOT_FOUND).body(error));
    }

    @ExceptionHandler(WebExchangeBindException.class)
    public Mono<ResponseEntity<ErrorResponse>> handleValidationException(
            WebExchangeBindException ex,
            ServerWebExchange exchange) {

        log.error("Validation error: {}", ex.getMessage());

        String message = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .reduce((a, b) -> a + ", " + b)
                .orElse("Error de validación");

        ErrorResponse error = buildErrorResponse(
                HttpStatus.BAD_REQUEST,
                message,
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error));
    }

    @ExceptionHandler(Exception.class)
    public Mono<ResponseEntity<ErrorResponse>> handleGenericException(
            Exception ex,
            ServerWebExchange exchange) {

        log.error("Unexpected error: {}", ex.getMessage(), ex);

        ErrorResponse error = buildErrorResponse(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Error interno del servidor",
                exchange.getRequest().getPath().value()
        );

        return Mono.just(ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error));
    }

    private ErrorResponse buildErrorResponse(HttpStatus status, String message, String path) {
        ErrorResponse error = new ErrorResponse();
        error.setTimestamp(OffsetDateTime.now());
        error.setStatus(status.value());
        error.setError(status.getReasonPhrase());
        error.setMessage(message);
        error.setPath(path);
        return error;
    }
}