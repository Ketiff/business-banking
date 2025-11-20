package com.bank.account_service.infrastructure.input.rest.controller;

import com.bank.account_service.application.ports.input.MovementUseCase;
import com.bank.account_service.infrastructure.input.rest.api.MovementsApi;
import com.bank.account_service.infrastructure.input.rest.mapper.MovementRestMapper;
import com.bank.account_service.infrastructure.input.rest.model.CreateMovementRequest;
import com.bank.account_service.infrastructure.input.rest.model.MovementResponse;
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
public class MovementController implements MovementsApi {

    private final MovementUseCase movementUseCase;
    private final MovementRestMapper movementMapper;

    @Override
    public Mono<ResponseEntity<Flux<MovementResponse>>> getAllMovements() {
        log.info("GET /api/v1/movements - List all movements");

        return Mono.just(ResponseEntity.ok(
                movementUseCase.findAll()
                        .map(movementMapper::toResponse)
        ));
    }

    @Override
    public Mono<ResponseEntity<MovementResponse>> createMovement(CreateMovementRequest createMovementRequest) {
        log.info("POST /api/v1/movements - Register movement for account: {}",
                createMovementRequest.getAccountId());

        var movement = movementMapper.toDomain(createMovementRequest);

        return movementUseCase.registerMovement(movement)
                .map(movementMapper::toResponse)
                .map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
    }

    @Override
    public Mono<ResponseEntity<MovementResponse>> getMovementById(Long id) {
        log.info("GET /api/v1/movements/{} - Get movement by ID", id);

        return movementUseCase.findById(id)
                .map(movementMapper::toResponse)
                .map(ResponseEntity::ok);
    }

    @Override
    public Mono<ResponseEntity<Void>> deleteMovement(Long id) {
        log.info("DELETE /api/v1/movements/{} - Delete movement", id);

        return movementUseCase.deleteMovement(id)
                .then(Mono.just(ResponseEntity.noContent().<Void>build()));
    }

    @Override
    public Mono<ResponseEntity<Flux<MovementResponse>>> getMovementsByAccountId(Long accountId) {
        log.info("GET /api/v1/movements/account/{} - Get movements by account", accountId);

        return Mono.just(ResponseEntity.ok(
                movementUseCase.findByAccountId(accountId)
                        .map(movementMapper::toResponse)
        ));
    }
}