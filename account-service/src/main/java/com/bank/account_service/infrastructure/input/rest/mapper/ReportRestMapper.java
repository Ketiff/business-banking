package com.bank.account_service.infrastructure.input.rest.mapper;

import com.bank.account_service.application.dto.AccountStatementDto;
import com.bank.account_service.infrastructure.input.rest.model.AccountStatementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface ReportRestMapper {

    /**
     * Convert application DTO to REST response
     */
    AccountStatementResponse toResponse(AccountStatementDto dto);

    /**
     * Convert String (date-time) to OffsetDateTime
     */
    default OffsetDateTime map(String dateString) {
        if (dateString == null) {
            return null;
        }
        try {
            return LocalDateTime.parse(dateString, DateTimeFormatter.ISO_DATE_TIME)
                    .atOffset(ZoneOffset.UTC);
        } catch (Exception e) {
            return null;
        }
    }
}