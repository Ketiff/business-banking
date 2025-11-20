package com.bank.account_service.application.ports.input;

import com.bank.account_service.domain.model.Account;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface AccountUseCase {

    Mono<Account> createAccount(Account account);

    Mono<Account> updateAccount(Long id, Account account);

    Mono<Account> findById(Long id);

    Mono<Account> findByAccountNumber(String accountNumber);

    Flux<Account> findByCustomerId(Long customerId);

    Flux<Account> findAll();

    Mono<Void> deleteAccount(Long id);
}