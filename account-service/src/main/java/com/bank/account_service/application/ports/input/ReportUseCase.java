package com.bank.account_service.application.ports.input;

import com.bank.account_service.domain.model.AccountStatement;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

public interface ReportUseCase {
    Mono<AccountStatement> generateAccountStatement(Long customerId, LocalDate startDate, LocalDate endDate);
}