package com.bank.account_service.domain.exceptions;

public class AccountExceptions {

    public static class AccountNotFoundException extends RuntimeException {
        public AccountNotFoundException(String message) {
            super(message);
        }
    }

    public static class InsufficientBalanceException extends RuntimeException {
        // F3
        private static final String MESSAGE = "Saldo no disponible";

        public InsufficientBalanceException() {
            super(MESSAGE);
        }

        public InsufficientBalanceException(String additionalInfo) {
            super(MESSAGE + ". " + additionalInfo);
        }
    }

    public static class InvalidMovementException extends RuntimeException {
        public InvalidMovementException(String message) {
            super(message);
        }
    }

    public static class InactiveAccountException extends RuntimeException {
        public InactiveAccountException(String message) {
            super(message);
        }
    }

    public static class InvalidAccountDataException extends RuntimeException {
        public InvalidAccountDataException(String message) {
            super(message);
        }
    }

    public static class MovementNotFoundException extends RuntimeException {
        public MovementNotFoundException(String message) {
            super(message);
        }
    }
}