package com.bank.account_service.infrastructure.input.rest.mapper;

import com.bank.account_service.domain.model.Movement;
import com.bank.account_service.infrastructure.input.rest.model.CreateMovementRequest;
import com.bank.account_service.infrastructure.input.rest.model.MovementResponse;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;

import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;

@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface MovementRestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "date", ignore = true)
    @Mapping(target = "balance", ignore = true)
    Movement toDomain(CreateMovementRequest request);

    MovementResponse toResponse(Movement domain);

    // Conversores de fecha
    default OffsetDateTime map(LocalDateTime localDateTime) {
        return localDateTime == null ? null : localDateTime.atOffset(ZoneOffset.UTC);
    }

    default LocalDateTime map(OffsetDateTime offsetDateTime) {
        return offsetDateTime == null ? null : offsetDateTime.toLocalDateTime();
    }
}