package com.bank.account_service.application.ports.input;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountStatementDto {

    private Long clientId;
    private String clientName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<AccountDetailDto> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDetailDto {
        private String accountNumber;
        private String accountType;
        private Double initialBalance;
        private Double currentBalance;
        private Boolean status;
        private List<MovementDetailDto> movements;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementDetailDto {
        private String date;
        private String movementType;
        private Double amount;
        private Double balance;
    }
}