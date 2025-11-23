package com.bank.account_service.infrastructure.output.persistence.adapter;

import com.bank.account_service.application.ports.output.MovementPersistencePort;
import com.bank.account_service.domain.model.Movement;
import com.bank.account_service.infrastructure.output.persistence.mapper.MovementPersistenceMapper;
import com.bank.account_service.infrastructure.output.persistence.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
@Slf4j
public class MovementPersistenceAdapter implements MovementPersistencePort {

    private final MovementRepository movementRepository;
    private final MovementPersistenceMapper movementMapper;

    @Override
    public Mono<Movement> save(Movement movement) {
        log.debug("Saving movement: {}", movement);
        return Mono.fromCallable(() -> {
            var entity = movementMapper.toEntity(movement);
            var saved = movementRepository.save(entity);
            return movementMapper.toDomain(saved);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Movement> findById(Long id) {
        log.debug("Finding movement by id: {}", id);
        return Mono.fromCallable(() -> movementRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.isPresent()
                        ? Mono.just(movementMapper.toDomain(optional.get()))
                        : Mono.empty());
    }

    @Override
    public Flux<Movement> findAll() {
        log.debug("Finding all movements");
        return Flux.defer(() -> Flux.fromIterable(movementRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(movementMapper::toDomain);
    }

    @Override
    public Flux<Movement> findByAccountId(Long accountId) {
        log.debug("Finding movements by account id: {}", accountId);
        return Flux.defer(() -> Flux.fromIterable(movementRepository.findByAccountId(accountId)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(movementMapper::toDomain);
    }

    @Override
    public Flux<Movement> findByAccountIdAndDateBetween(Long accountId,
                                                        LocalDateTime startDate,
                                                        LocalDateTime endDate) {
        log.debug("Finding movements for account: {} between {} and {}", accountId, startDate, endDate);
        return Flux.defer(() -> Flux.fromIterable(
                        movementRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(movementMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        log.debug("Deleting movement by id: {}", id);
        return Mono.fromRunnable(() -> movementRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}