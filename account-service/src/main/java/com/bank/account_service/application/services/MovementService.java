package com.bank.account_service.application.services;

import com.bank.account_service.application.ports.input.MovementUseCase;
import com.bank.account_service.application.ports.output.AccountPersistencePort;
import com.bank.account_service.application.ports.output.MovementPersistencePort;
import com.bank.account_service.domain.exceptions.AccountExceptions;
import com.bank.account_service.domain.model.Account;
import com.bank.account_service.domain.model.Movement;
import com.bank.account_service.domain.model.MovementType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class MovementService implements MovementUseCase {

    private final MovementPersistencePort movementPersistence;
    private final AccountPersistencePort accountPersistence;

    @Override
    public Mono<Movement> registerMovement(Movement movement) {
        log.info("Registering movement for account: {}", movement.getAccountId());

        // monto sea mayor a 0
        if (!Movement.isValidAmount(movement.getAmount())) {
            return Mono.error(new AccountExceptions.InvalidMovementException(
                    "El valor del movimiento debe ser mayor a cero"));
        }

        return accountPersistence.findById(movement.getAccountId())
                .switchIfEmpty(Mono.error(
                        new AccountExceptions.AccountNotFoundException("Cuenta con ID " + movement.getAccountId() + " no encontrada")))
                .flatMap(account -> processMovement(account, movement))
                .doOnSuccess(saved -> log.info("Movement registered successfully: {}", saved.getId()))
                .doOnError(error -> log.error("Error registering movement: {}", error.getMessage()));
    }

    private Mono<Movement> processMovement(Account account, Movement movement) {
        // Validar cuenta activa
        if (!account.isActive()) {
            return Mono.error(new AccountExceptions.InactiveAccountException(
                    "La cuenta " + account.getAccountNumber() + " no está activa"));
        }
        // F2: Calcular nuevo saldo
        BigDecimal newBalance = Movement.calculateNewBalance(
                account.getCurrentBalance(),
                movement.getMovementType(),
                movement.getAmount()
        );
        // F3: Validar saldo disponible para DEBIT
        if (movement.getMovementType() == MovementType.DEBIT) {
            if (!account.hasSufficientBalance(movement.getAmount())) {
                return Mono.error(new AccountExceptions.InsufficientBalanceException());
            }
        }
        account.updateBalance(newBalance);
        movement.setDate(LocalDateTime.now());
        movement.setBalance(newBalance);
        return accountPersistence.save(account)
                .then(movementPersistence.save(movement));
    }

    @Override
    public Mono<Movement> findById(Long id) {
        log.debug("Finding movement by id: {}", id);

        return movementPersistence.findById(id)
                .switchIfEmpty(Mono.error(
                        new AccountExceptions.MovementNotFoundException("Movimiento con ID " + id + " no encontrado")));
    }

    @Override
    public Flux<Movement> findByAccountId(Long accountId) {
        log.debug("Finding movements by account id: {}", accountId);
        return movementPersistence.findByAccountId(accountId);
    }

    @Override
    public Flux<Movement> findAll() {
        log.debug("Finding all movements");
        return movementPersistence.findAll();
    }

    @Override
    public Mono<Void> deleteMovement(Long id) {
        log.info("Deleting movement: {}", id);

        return movementPersistence.findById(id)
                .switchIfEmpty(Mono.error(
                        new AccountExceptions.MovementNotFoundException("Movimiento con ID " + id + " no encontrado")))
                .flatMap(movement -> movementPersistence.deleteById(id))
                .doOnSuccess(v -> log.info("Movement deleted successfully: {}", id));
    }
}