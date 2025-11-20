package com.bank.account_service.infrastructure.input.rest.mapper;

import com.bank.account_service.domain.model.Movement;
import com.bank.account_service.infrastructure.input.rest.model.CreateMovementRequest;
import com.bank.account_service.infrastructure.input.rest.model.MovementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovementRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "balance", ignore = true)
    Movement toDomain(CreateMovementRequest request);

    MovementResponse toResponse(Movement domain);
}