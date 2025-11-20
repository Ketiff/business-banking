package com.bank.account_service.infrastructure.output.client;

import com.bank.account_service.application.ports.output.CustomerClientPort;
import com.bank.account_service.infrastructure.output.client.dto.CustomerDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
@Slf4j
public class CustomerClientAdapter implements CustomerClientPort {

    private final WebClient customerWebClient;

    @Override
    public Mono<Boolean> existsCustomer(Long customerId) {
        log.debug("Checking if customer exists: {}", customerId);

        return customerWebClient
                .get()
                .uri("/api/v1/customers/{id}", customerId)
                .retrieve()
                .toBodilessEntity()
                .map(response -> response.getStatusCode() == HttpStatus.OK)
                .onErrorResume(error -> {
                    log.warn("Customer {} not found or service unavailable: {}",
                            customerId, error.getMessage());
                    return Mono.just(false);
                });
    }

    @Override
    public Mono<CustomerClientPort.CustomerDto> getCustomerById(Long customerId) {
        log.debug("Getting customer by id: {}", customerId);

        return customerWebClient
                .get()
                .uri("/api/v1/customers/{id}", customerId)
                .retrieve()
                .bodyToMono(CustomerDto.class)
                .map(this::mapToPortDto)
                .doOnSuccess(customer -> log.debug("Customer found: {}", customer.getName()))
                .onErrorResume(error -> {
                    log.error("Error getting customer {}: {}", customerId, error.getMessage());
                    return Mono.empty();
                });
    }

    private CustomerClientPort.CustomerDto mapToPortDto(CustomerDto dto) {
        return CustomerClientPort.CustomerDto.builder()
                .id(dto.getId())
                .name(dto.getName())
                .identification(dto.getIdentification())
                .status(dto.getStatus())
                .build();
    }
}
