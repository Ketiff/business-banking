package com.bank.account_service.application.ports.input;

import com.bank.account_service.application.dto.AccountStatementDto;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReportUseCase {
    Mono<AccountStatementDto> generateAccountStatement(Long customerId, LocalDate startDate, LocalDate endDate);
}