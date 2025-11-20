package com.bank.account_service.application.ports.output;

import reactor.core.publisher.Mono;

public interface CustomerClientPort {

    Mono<Boolean> existsCustomer(Long customerId);

    Mono<CustomerDto> getCustomerById(Long customerId);

    @lombok.Data
    @lombok.Builder
    @lombok.NoArgsConstructor
    @lombok.AllArgsConstructor
    class CustomerDto {
        private Long id;
        private String name;
        private String identification;
        private Boolean status;
    }
}