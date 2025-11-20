package com.bank.account_service.infrastructure.output.persistence.mapper;

import com.bank.account_service.domain.model.Movement;
import com.bank.account_service.infrastructure.output.persistence.entity.MovementEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovementPersistenceMapper {

    Movement toDomain(MovementEntity entity);

    MovementEntity toEntity(Movement domain);
}