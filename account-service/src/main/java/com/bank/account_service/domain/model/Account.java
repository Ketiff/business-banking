package com.bank.account_service.domain.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    private Long id;
    private String accountNumber;
    private AccountType accountType;
    private BigDecimal initialBalance;
    private BigDecimal currentBalance;
    private Boolean status;
    private Long customerId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    // Método de negocio: validar si la cuenta está activa
    public boolean isActive() {
        return Boolean.TRUE.equals(status);
    }

    // Método de negocio: validar si tiene saldo suficiente
    public boolean hasSufficientBalance(BigDecimal amount) {
        return currentBalance.compareTo(amount) >= 0;
    }

    // Método de negocio: actualizar saldo
    public void updateBalance(BigDecimal newBalance) {
        this.currentBalance = newBalance;
        this.updatedAt = LocalDateTime.now();
    }
}