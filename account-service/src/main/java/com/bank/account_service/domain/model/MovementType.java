package com.bank.account_service.domain.model;

public enum MovementType {

    DEBIT("Débito", -1),
    CREDIT("Crédito", 1);

    private final String displayName;
    private final int multiplier;

    MovementType(String displayName, int multiplier) {
        this.displayName = displayName;
        this.multiplier = multiplier;
    }

    // nuevoSaldo = saldoActual + (monto * multiplier)
    public int getMultiplier() {
        return multiplier;
    }

    //@return true si es DEBIT (requiere validación), false si es CREDIT
    public boolean requiresBalanceValidation() {
        return this == DEBIT;
    }
}