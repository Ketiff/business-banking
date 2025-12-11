package com.bank.account_service.application.services;

import com.bank.account_service.application.ports.input.AccountUseCase;
import com.bank.account_service.application.ports.output.AccountPersistencePort;
import com.bank.account_service.application.ports.output.CustomerClientPort;
import com.bank.account_service.domain.exceptions.AccountExceptions;
import com.bank.account_service.domain.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountService implements AccountUseCase {

    private final AccountPersistencePort accountPersistence;
    private final CustomerClientPort customerPort;

    @Override
    public Mono<Account> createAccount(Account account) {
        log.info("Creating account: {}", account.getAccountNumber());

        return customerPort.existsCustomer(account.getCustomerId())
                .flatMap(exists -> {
                    if (!exists) {
                        return Mono.error(new AccountExceptions.InvalidAccountDataException(
                                "Cliente con ID " + account.getCustomerId() + " no encontrado"));
                    }

                    return accountPersistence.existsByAccountNumber(account.getAccountNumber())
                            .flatMap(numberExists -> {
                                if (numberExists) {
                                    return Mono.error(new AccountExceptions.InvalidAccountDataException(
                                            "Ya existe una cuenta con el número " + account.getAccountNumber()));
                                }

                                account.setCurrentBalance(account.getInitialBalance());
                                account.setStatus(true);
                                account.setCreatedAt(LocalDateTime.now());
                                account.setUpdatedAt(LocalDateTime.now());

                                return accountPersistence.save(account);
                            });
                })
                .doOnSuccess(created -> log.info("Account created successfully: {}", created.getId()))
                .doOnError(error -> log.error("Error creating account: {}", error.getMessage()));
    }

    @Override
    public Mono<Account> updateAccount(Long id, Account account) {
        log.info("Updating account: {}", id);

        return accountPersistence.findById(id)
                .switchIfEmpty(Mono.error(
                        new AccountExceptions.AccountNotFoundException("Cuenta con ID " + id + " no encontrada")))
                .flatMap(existing -> {
                    if (account.getAccountType() != null) {
                        existing.setAccountType(account.getAccountType());
                    }
                    if (account.getStatus() != null) {
                        existing.setStatus(account.getStatus());
                    }
                    existing.setUpdatedAt(LocalDateTime.now());

                    return accountPersistence.save(existing);
                })
                .doOnSuccess(updated -> log.info("Account updated successfully: {}", id));
    }

    @Override
    public Mono<Account> findById(Long id) {
        log.debug("Finding account by id: {}", id);

        return accountPersistence.findById(id)
                .switchIfEmpty(Mono.error(
                        new AccountExceptions.AccountNotFoundException("Cuenta con ID " + id + " no encontrada")));
    }

    @Override
    public Mono<Account> findByAccountNumber(String accountNumber) {
        log.debug("Finding account by number: {}", accountNumber);

        return accountPersistence.findByAccountNumber(accountNumber)
                .switchIfEmpty(Mono.error(
                        new AccountExceptions.AccountNotFoundException("Cuenta número " + accountNumber + " no encontrada")));
    }

    @Override
    public Flux<Account> findByCustomerId(Long customerId) {
        log.debug("Finding accounts by customer id: {}", customerId);
        return accountPersistence.findByCustomerId(customerId);
    }

    @Override
    public Flux<Account> findAll() {
        log.debug("Finding all accounts");
        return accountPersistence.findAll();
    }

    @Override
    public Mono<Void> deleteAccount(Long id) {
        log.info("Deleting account: {}", id);

        return accountPersistence.findById(id)
                .switchIfEmpty(Mono.error(
                        new AccountExceptions.AccountNotFoundException("Cuenta con ID " + id + " no encontrada")))
                .flatMap(account -> accountPersistence.deleteById(id))
                .doOnSuccess(v -> log.info("Account deleted successfully: {}", id));
    }
}