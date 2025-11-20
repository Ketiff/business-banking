package com.bank.account_service.infrastructure.output.persistence.adapter;

import com.bank.account_service.application.ports.output.MovementPersistencePort;
import com.bank.account_service.domain.model.Movement;
import com.bank.account_service.infrastructure.output.persistence.mapper.MovementPersistenceMapper;
import com.bank.account_service.infrastructure.output.persistence.repository.MovementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class MovementPersistenceAdapter implements MovementPersistencePort {

    private final MovementRepository movementRepository;
    private final MovementPersistenceMapper movementMapper;

    @Override
    public Mono<Movement> save(Movement movement) {
        return Mono.fromCallable(() -> {
            var entity = movementMapper.toEntity(movement);
            var saved = movementRepository.save(entity);
            return movementMapper.toDomain(saved);
        }).subscribeOn(Schedulers.boundedElastic());
    }

    @Override
    public Mono<Movement> findById(Long id) {
        return Mono.fromCallable(() -> movementRepository.findById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .flatMap(optional -> optional.isPresent()
                        ? Mono.just(movementMapper.toDomain(optional.get()))
                        : Mono.empty());
    }

    @Override
    public Flux<Movement> findByAccountId(Long accountId) {
        return Flux.defer(() -> Flux.fromIterable(movementRepository.findByAccountId(accountId)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(movementMapper::toDomain);
    }

    @Override
    public Flux<Movement> findByAccountIdAndDateBetween(Long accountId,
                                                        LocalDateTime startDate,
                                                        LocalDateTime endDate) {
        return Flux.defer(() -> Flux.fromIterable(
                        movementRepository.findByAccountIdAndDateBetween(accountId, startDate, endDate)))
                .subscribeOn(Schedulers.boundedElastic())
                .map(movementMapper::toDomain);
    }

    @Override
    public Flux<Movement> findAll() {
        return Flux.defer(() -> Flux.fromIterable(movementRepository.findAll()))
                .subscribeOn(Schedulers.boundedElastic())
                .map(movementMapper::toDomain);
    }

    @Override
    public Mono<Void> deleteById(Long id) {
        return Mono.fromRunnable(() -> movementRepository.deleteById(id))
                .subscribeOn(Schedulers.boundedElastic())
                .then();
    }
}