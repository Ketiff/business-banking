package com.bank.account_service.infrastructure.input.rest.mapper;

import com.bank.account_service.application.ports.input.AccountStatementDto;
import com.bank.account_service.infrastructure.input.rest.model.AccountStatementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReportRestMapper {

    AccountStatementResponse toResponse(AccountStatementDto domain);

    // Método custom para mapear String (del DTO interno) a OffsetDateTime (del modelo OpenAPI generado)
    default OffsetDateTime map(String value) {
        if (value == null) {
            return null;
        }
        try {
            // Intenta parsear directamente (si incluye la zona)
            return OffsetDateTime.parse(value);
        } catch (DateTimeParseException e) {
            // Si no tiene zona (ej. LocalDateTime.toString()), se asume la zona por defecto del sistema
            return java.time.LocalDateTime.parse(value)
                    .atZone(java.time.ZoneId.systemDefault())
                    .toOffsetDateTime();
        }
    }
}