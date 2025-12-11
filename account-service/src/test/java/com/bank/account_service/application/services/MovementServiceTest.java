package com.bank.account_service.application.services;

import com.bank.account_service.application.ports.output.AccountPersistencePort;
import com.bank.account_service.application.ports.output.MovementPersistencePort;
import com.bank.account_service.domain.exceptions.AccountExceptions.InsufficientBalanceException;
import com.bank.account_service.domain.model.Account;
import com.bank.account_service.domain.model.Movement;
import com.bank.account_service.domain.model.MovementType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.math.BigDecimal;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MovementServiceTest {

    @Mock
    private MovementPersistencePort movementPersistence;
    @Mock
    private AccountPersistencePort accountPersistence;

    @InjectMocks
    private MovementService movementService;

    @Test
    void registerMovement_Credit_Success() {
        // Arrange
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        account.setCurrentBalance(new BigDecimal("100.00"));
        account.setStatus(true);

        Movement movement = new Movement();
        movement.setAccountId(accountId);
        movement.setMovementType(MovementType.CREDIT);
        movement.setAmount(new BigDecimal("50.00"));

        when(accountPersistence.findById(accountId)).thenReturn(Mono.just(account));
        when(accountPersistence.save(any(Account.class))).thenReturn(Mono.just(account));
        when(movementPersistence.save(any(Movement.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        // Act & Assert
        StepVerifier.create(movementService.registerMovement(movement))
                .expectNextMatches(savedMovement -> 
                    savedMovement.getBalance().compareTo(new BigDecimal("150.00")) == 0
                )
                .verifyComplete();

        verify(accountPersistence).save(argThat(a -> 
            a.getCurrentBalance().compareTo(new BigDecimal("150.00")) == 0
        ));
    }

    @Test
    void registerMovement_Debit_Success() {
        // Arrange
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        account.setCurrentBalance(new BigDecimal("100.00"));
        account.setStatus(true);

        Movement movement = new Movement();
        movement.setAccountId(accountId);
        movement.setMovementType(MovementType.DEBIT);
        movement.setAmount(new BigDecimal("50.00"));

        when(accountPersistence.findById(accountId)).thenReturn(Mono.just(account));
        when(accountPersistence.save(any(Account.class))).thenReturn(Mono.just(account));
        when(movementPersistence.save(any(Movement.class))).thenAnswer(i -> Mono.just(i.getArgument(0)));

        // Act & Assert
        StepVerifier.create(movementService.registerMovement(movement))
                .expectNextMatches(savedMovement -> 
                    savedMovement.getBalance().compareTo(new BigDecimal("50.00")) == 0
                )
                .verifyComplete();
    }

    @Test
    void registerMovement_Debit_InsufficientBalance_ThrowsException() {
        // Arrange
        Long accountId = 1L;
        Account account = new Account();
        account.setId(accountId);
        account.setCurrentBalance(new BigDecimal("100.00")); // Tiene 100
        account.setStatus(true);

        Movement movement = new Movement();
        movement.setAccountId(accountId);
        movement.setMovementType(MovementType.DEBIT);
        movement.setAmount(new BigDecimal("200.00")); // Quiere sacar 200

        when(accountPersistence.findById(accountId)).thenReturn(Mono.just(account));

        // Act & Assert
        StepVerifier.create(movementService.registerMovement(movement))
                .expectError(InsufficientBalanceException.class) // Esperamos la excepción F3
                .verify();

        // Verificamos que NUNCA se guardó nada
        verify(accountPersistence, never()).save(any());
        verify(movementPersistence, never()).save(any());
    }
}