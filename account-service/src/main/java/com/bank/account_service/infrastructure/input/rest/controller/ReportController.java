package com.bank.account_service.infrastructure.input.rest.controller;

import com.bank.account_service.application.ports.input.ReportUseCase;
import com.bank.account_service.infrastructure.input.rest.api.ReportsApi;
import com.bank.account_service.infrastructure.input.rest.mapper.ReportRestMapper;
import com.bank.account_service.infrastructure.input.rest.model.AccountStatementResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Mono;

import java.time.LocalDate;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ReportController implements ReportsApi {

    private final ReportUseCase reportUseCase;
    private final ReportRestMapper reportMapper;

    @Override
    public Mono<ResponseEntity<AccountStatementResponse>> getAccountStatement(
            Long clientId,
            LocalDate startDate,
            LocalDate endDate,
            ServerWebExchange exchange) {

        log.info("GET /api/v1/reports/{} - Generate account statement from {} to {}",
                clientId, startDate, endDate);

        return reportUseCase.generateAccountStatement(clientId, startDate, endDate)
                .map(reportMapper::toResponse)
                .map(ResponseEntity::ok);
    }
}