package com.bank.account_service.application.ports.input;

import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReportUseCase {

    Mono<AccountStatementDto> generateAccountStatement(Long clientId,
                                                       LocalDate startDate,
                                                       LocalDate endDate);
}