package com.bank.account_service.domain.model;

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
public class AccountStatement {
    private Long customerId;
    private String customerName;
    private LocalDate startDate;
    private LocalDate endDate;
    private List<AccountDetail> accounts;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class AccountDetail {
        private String accountNumber;
        private String accountType;
        private Double initialBalance;
        private Double currentBalance;
        private List<MovementDetail> movements;
    }

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MovementDetail {
        private String date;
        private String movementType;
        private Double amount;
        private Double balance;
    }
}