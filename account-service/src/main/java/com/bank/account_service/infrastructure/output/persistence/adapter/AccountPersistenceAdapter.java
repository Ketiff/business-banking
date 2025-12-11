 package com.bank.account_service.infrastructure.output.persistence.adapter;

import com.bank.account_service.application.ports.output.AccountPersistencePort;
import com.bank.account_service.domain.model.Account;
import com.bank.account_service.infrastructure.output.persistence.mapper.AccountPersistenceMapper;
import com.bank.account_service.infrastructure.output.persistence.repository.AccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

@Component
@RequiredArgsConstructor
public class AccountPersistenceAdapter implements AccountPersistencePort {

    private final AccountRepository accountRepository;
    private final AccountPersistenceMapper accountMapper;

    @Override
    public Mono<Account> save(Account account) {
        return Mono.fromCallable(() -> {
            var entity = accountMapper.toEntity(account);
            var saved = accountRepository.save(entity);
            return accountMapper.toDomain(saved);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Account> findById(Long id) {
        return Mono.fromCallable(() -> accountRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.isPresent()
                        ? Mono.just(accountMapper.toDomain(optional.get()))
                        : Mono.empty());
    }

    @Override
    public Mono<Account> findByAccountNumber(String accountNumber) {
        return Mono.fromCallable(() -> accountRepository.findByAccountNumber(accountNumber))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.isPresent()
                        ? Mono.just(accountMapper.toDomain(optional.get()))
                        : Mono.empty());
    }

    @Override
    public Flux<Account> findByCustomerId(Long customerId) {
        return Flux.defer(() -> Flux.fromIterable(accountRepository.findByCustomerId(customerId)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(accountMapper::toDomain);
    }

    @Override
    public Flux<Account> findAll() {
        return Flux.defer(() -> Flux.fromIterable(accountRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(accountMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> accountRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }

    @Override
    public Mono<Boolean> existsByAccountNumber(String accountNumber) {
        return Mono.fromCallable(() -> accountRepository.existsByAccountNumber(accountNumber))
                .subscribeOn(Schedulers.boundedElastic());
    }
}