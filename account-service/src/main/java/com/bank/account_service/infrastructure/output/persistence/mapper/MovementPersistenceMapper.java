package com.bank.account_service.infrastructure.output.persistence.mapper;

import com.bank.account_service.domain.model.Movement;
import com.bank.account_service.infrastructure.output.persistence.entity.AccountEntity;
import com.bank.account_service.infrastructure.output.persistence.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovementPersistenceMapper {

    @Mapping(source = "account.id", target = "accountId")
    Movement toDomain(MovementEntity entity);

    @Mapping(target = "account", expression = "java(createAccountReference(domain.getAccountId()))")
    MovementEntity toEntity(Movement domain);

    default AccountEntity createAccountReference(Long accountId) {
        if (accountId == null) {
            return null;
        }
        AccountEntity account = new AccountEntity();
        account.setId(accountId);
        return account;
    }
}