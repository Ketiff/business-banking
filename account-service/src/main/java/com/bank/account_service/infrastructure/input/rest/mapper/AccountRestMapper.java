package com.bank.account_service.infrastructure.input.rest.mapper;

import com.bank.account_service.domain.model.Account;
import com.bank.account_service.infrastructure.input.rest.model.AccountResponse;
import com.bank.account_service.infrastructure.input.rest.model.CreateAccountRequest;
import com.bank.account_service.infrastructure.input.rest.model.UpdateAccountRequest;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "currentBalance", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    Account toDomain(CreateAccountRequest request);

    Account toDomain(UpdateAccountRequest request);

    AccountResponse toResponse(Account domain);
}