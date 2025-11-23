package com.bank.account_service.application.services;

import com.bank.account_service.application.ports.input.AccountStatementDto;
import com.bank.account_service.application.ports.input.AccountStatementDto.AccountDetailDto;
import com.bank.account_service.application.ports.input.AccountStatementDto.MovementDetailDto;
import com.bank.account_service.application.ports.input.ReportUseCase;
import com.bank.account_service.application.ports.output.AccountPersistencePort;
import com.bank.account_service.application.ports.output.CustomerClientPort; // Corregido el nombre
import com.bank.account_service.application.ports.output.MovementPersistencePort;
import com.bank.account_service.domain.exceptions.AccountExceptions;
import com.bank.account_service.domain.model.Account;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService implements ReportUseCase {

    private final CustomerClientPort customerClient;
    private final AccountPersistencePort accountPersistence;
    private final MovementPersistencePort movementPersistence;

    @Override
    public Mono<AccountStatementDto> generateAccountStatement(Long clientId,
                                                              LocalDate startDate,
                                                              LocalDate endDate) {
        log.info("Generating account statement for client: {} from {} to {}", clientId, startDate, endDate);

        // Ajustar fechas para cubrir el día completo
        LocalDateTime startDateTime = LocalDateTime.of(startDate, LocalTime.MIN);
        LocalDateTime endDateTime = LocalDateTime.of(endDate, LocalTime.MAX);

        return customerClient.getCustomerById(clientId)
                .switchIfEmpty(Mono.error(
                        new AccountExceptions.AccountNotFoundException("Cliente con ID " + clientId + " no encontrado para el reporte")))
                .flatMap(customer ->
                        accountPersistence.findByCustomerId(clientId)
                                .collectList()
                                .flatMap(accounts -> {
                                    if (accounts.isEmpty()) {
                                        return Mono.error(new AccountExceptions.AccountNotFoundException(
                                                "Cliente con ID " + clientId + " no tiene cuentas asociadas"));
                                    }
                                    return buildStatementDto(customer.getName(), clientId, startDate, endDate, accounts, startDateTime, endDateTime);
                                })
                )
                .doOnSuccess(report -> log.info("Report generated successfully for client: {}", clientId))
                .doOnError(error -> log.error("Error generating report for client {}: {}", clientId, error.getMessage()));
    }

    private Mono<AccountStatementDto> buildStatementDto(String clientName,
                                                        Long clientId,
                                                        LocalDate startDate,
                                                        LocalDate endDate,
                                                        List<Account> accounts,
                                                        LocalDateTime startDateTime,
                                                        LocalDateTime endDateTime) {

        // Procesar las cuentas y sus movimientos en paralelo
        List<Mono<AccountDetailDto>> detailMonos = accounts.stream()
                .map(account -> movementPersistence.findByAccountIdAndDateBetween(
                                account.getId(),
                                startDateTime,
                                endDateTime)
                        .map(movement -> MovementDetailDto.builder()
                                .date(movement.getDate().toString())
                                .movementType(movement.getMovementType().name())
                                .amount(movement.getAmount().doubleValue())
                                .balance(movement.getBalance().doubleValue())
                                .build())
                        .collectList()
                        .map(movementDetails -> AccountDetailDto.builder()
                                .accountNumber(account.getAccountNumber())
                                .accountType(account.getAccountType().name())
                                .initialBalance(account.getInitialBalance().doubleValue())
                                .currentBalance(account.getCurrentBalance().doubleValue())
                                .status(account.getStatus())
                                .movements(movementDetails)
                                .build())
                ).toList();

        return Flux.merge(detailMonos)
                .collectList()
                .map(accountDetails -> AccountStatementDto.builder()
                        .clientId(clientId)
                        .clientName(clientName)
                        .startDate(startDate)
                        .endDate(endDate)
                        .accounts(accountDetails)
                        .build());
    }
}