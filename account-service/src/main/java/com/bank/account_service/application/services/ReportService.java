package com.bank.account_service.application.services;

import com.bank.account_service.application.dto.AccountStatementDto;
import com.bank.account_service.application.ports.input.ReportUseCase;
import com.bank.account_service.application.ports.output.AccountPersistencePort;
import com.bank.account_service.application.ports.output.CustomerClientPort;
import com.bank.account_service.application.ports.output.MovementPersistencePort;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class ReportService implements ReportUseCase {

    private final AccountPersistencePort accountPersistencePort;
    private final MovementPersistencePort movementPersistencePort;
    private final CustomerClientPort customerClientPort;

    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ISO_DATE_TIME;

    @Override
    public Mono<AccountStatementDto> generateAccountStatement(Long customerId, LocalDate startDate, LocalDate endDate) {
        log.info("Generating account statement for customer: {}, from: {} to: {}", customerId, startDate, endDate);

        return customerClientPort.getCustomerById(customerId)
                .flatMap(customer -> {
                    log.debug("Customer found: {}", customer.getName());

                    return accountPersistencePort.findByCustomerId(customerId)
                            .flatMap(account -> {
                                log.debug("Processing account: {}", account.getAccountNumber());

                                return movementPersistencePort
                                        .findByAccountIdAndDateBetween(
                                                account.getId(),
                                                startDate.atStartOfDay(),
                                                endDate.atTime(23, 59, 59)
                                        )
                                        .collectList()
                                        .map(movements -> {
                                            log.debug("Found {} movements for account {}", movements.size(), account.getAccountNumber());

                                            var movementDetails = movements.stream()
                                                    .map(movement -> AccountStatementDto.MovementDetailDto.builder()
                                                            .date(movement.getDate().format(DATE_FORMATTER))
                                                            .movementType(movement.getMovementType().name())
                                                            .amount(movement.getAmount().doubleValue())  // ← BigDecimal → Double
                                                            .balance(movement.getBalance().doubleValue()) // ← BigDecimal → Double
                                                            .build())
                                                    .toList();

                                            return AccountStatementDto.AccountDetailDto.builder()
                                                    .accountNumber(account.getAccountNumber())
                                                    .accountType(account.getAccountType().name())
                                                    .initialBalance(account.getInitialBalance().doubleValue())   // ← BigDecimal → Double
                                                    .currentBalance(account.getCurrentBalance().doubleValue())   // ← BigDecimal → Double
                                                    .movements(movementDetails)
                                                    .build();
                                        });
                            })
                            .collectList()
                            .map(accountDetails -> {
                                log.info("Generated statement with {} accounts", accountDetails.size());

                                return AccountStatementDto.builder()
                                        .customerId(customerId)
                                        .customerName(customer.getName())
                                        .startDate(startDate)
                                        .endDate(endDate)
                                        .accounts(accountDetails) // ← Ya es List<AccountDetailDto>
                                        .build();
                            });
                });
    }
}