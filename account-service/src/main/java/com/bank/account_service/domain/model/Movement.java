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
public class Movement {

    private Long id;
    private LocalDateTime date;
    private MovementType movementType;
    private BigDecimal amount;
    private BigDecimal balance;
    private Long accountId;

    public static BigDecimal calculateNewBalance(BigDecimal currentBalance,
                                                 MovementType type,
                                                 BigDecimal amount) {
        return currentBalance.add(
                amount.multiply(BigDecimal.valueOf(type.getMultiplier()))
        );
    }

    public static boolean isValidAmount(BigDecimal amount) {
        return amount != null && amount.compareTo(BigDecimal.ZERO) > 0;
    }
}
