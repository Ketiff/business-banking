package com.bank.account_service.infrastructure.input.rest.mapper;

import com.bank.account_service.domain.model.Account;
import com.bank.account_service.infrastructure.input.rest.model.AccountResponse;
import com.bank.account_service.infrastructure.input.rest.model.CreateAccountRequest;
import com.bank.account_service.infrastructure.input.rest.model.UpdateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toDomain(CreateAccountRequest request);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "accountNumber", ignore = true)
    @Mapping(target = "initialBalance", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "customerId", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toDomain(UpdateAccountRequest request);

    AccountResponse toResponse(Account domain);

    // Conversores de fecha
    default OffsetDateTime map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}