package com.bank.account_service.application.ports.output;

import com.bank.account_service.domain.model.Movement;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface MovementPersistencePort {
    Mono<Movement> save(Movement movement);
    Mono<Movement> findById(Long id);
    Flux<Movement> findAll();
    Flux<Movement> findByAccountId(Long accountId);
    Flux<Movement> findByAccountIdAndDateBetween(Long accountId, LocalDateTime startDate, LocalDateTime endDate);
    Mono<Void> deleteById(Long id);
}