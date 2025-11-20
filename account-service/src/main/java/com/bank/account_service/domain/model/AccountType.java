package com.bank.account_service.domain.model;

public enum AccountType {

    SAVINGS("Ahorros"),
    CHECKING("Corriente");

    private final String displayName;

    AccountType(String displayName) {
        this.displayName = displayName;
    }

    /** Para mostrar nombre en UI **/
    public String getDisplayName() {
        return displayName;
    }
}