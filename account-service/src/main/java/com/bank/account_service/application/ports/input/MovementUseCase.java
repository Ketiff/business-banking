package com.bank.account_service.application.ports.input;

import com.bank.account_service.domain.model.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface MovementUseCase {

    Mono<Movement> registerMovement(Movement movement);

    Mono<Movement> findById(Long id);

    Flux<Movement> findByAccountId(Long accountId);

    Flux<Movement> findAll();

    Mono<Void> deleteMovement(Long id);
}