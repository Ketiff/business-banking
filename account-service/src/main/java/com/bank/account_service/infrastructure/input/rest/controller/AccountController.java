package com.bank.account_service.infrastructure.input.rest.controller;

import com.bank.account_service.application.ports.input.AccountUseCase;
import com.bank.account_service.infrastructure.input.rest.api.AccountsApi;
import com.bank.account_service.infrastructure.input.rest.mapper.AccountRestMapper;
import com.bank.account_service.infrastructure.input.rest.model.AccountResponse;
import com.bank.account_service.infrastructure.input.rest.model.CreateAccountRequest;
import com.bank.account_service.infrastructure.input.rest.model.UpdateAccountRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequiredArgsConstructor
@Slf4j
public class AccountController implements AccountsApi {

    private final AccountUseCase accountUseCase;
    private final AccountRestMapper accountMapper;

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> getAllAccounts() {
        log.info("GET /api/v1/accounts - List all accounts");

        return Mono.just(ResponseEntity.ok(
                accountUseCase.findAll()
                        .map(accountMapper::toResponse)
        ));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> createAccount(CreateAccountRequest createAccountRequest) {
        log.info("POST /api/v1/accounts - Create account: {}", createAccountRequest.getAccountNumber());

        var account = accountMapper.toDomain(createAccountRequest);

        return accountUseCase.createAccount(account)
                .map(accountMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> getAccountById(Long id) {
        log.info("GET /api/v1/accounts/{} - Get account by ID", id);

        return accountUseCase.findById(id)
                .map(accountMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> updateAccount(Long id, UpdateAccountRequest updateAccountRequest) {
        log.info("PUT /api/v1/accounts/{} - Update account", id);

        var account = accountMapper.toDomain(updateAccountRequest);

        return accountUseCase.updateAccount(id, account)
                .map(accountMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteAccount(Long id) {
        log.info("DELETE /api/v1/accounts/{} - Delete account", id);

        return accountUseCase.deleteAccount(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Override
    public Mono<ResponseEntity<AccountResponse>> getAccountByNumber(String accountNumber) {
        log.info("GET /api/v1/accounts/number/{} - Get account by number", accountNumber);

        return accountUseCase.findByAccountNumber(accountNumber)
                .map(accountMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Flux<AccountResponse>>> getAccountsByCustomerId(Long customerId) {
        log.info("GET /api/v1/accounts/customer/{} - Get accounts by customer", customerId);

        return Mono.just(ResponseEntity.ok(
                accountUseCase.findByCustomerId(customerId)
                        .map(accountMapper::toResponse)
        ));
    }
}