package com.bank.account_service.infrastructure.output.persistence.mapper;

import com.bank.account_service.domain.model.Account;
import com.bank.account_service.infrastructure.output.persistence.entity.AccountEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface AccountPersistenceMapper {

    Account toDomain(AccountEntity entity);
    AccountEntity toEntity(Account domain);
}