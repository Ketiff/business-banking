package com.bank.account_service.domain.model;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class MovementTypeTest {

    @Test
    void debitShouldHaveNegativeMultiplier() {
        assertEquals(-1, MovementType.DEBIT.getMultiplier());
    }

    @Test
    void creditShouldHavePositiveMultiplier() {
        assertEquals(1, MovementType.CREDIT.getMultiplier());
    }

    @Test
    void debitShouldRequireBalanceValidation() {
        assertTrue(MovementType.DEBIT.requiresBalanceValidation());
    }

    @Test
    void creditShouldNotRequireBalanceValidation() {
        assertFalse(MovementType.CREDIT.requiresBalanceValidation());
    }
}