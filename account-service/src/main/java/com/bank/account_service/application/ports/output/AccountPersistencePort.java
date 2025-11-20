package com.bank.account_service.application.ports.output;

import com.bank.account_service.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountPersistencePort {

    Mono<Account> save(Account account);

    Mono<Account> findById(Long id);

    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findByCustomerId(Long customerId);

    Flux<Account> findAll();

    Mono<Void> deleteById(Long id);

    Mono<Boolean> existsByAccountNumber(String accountNumber);
}