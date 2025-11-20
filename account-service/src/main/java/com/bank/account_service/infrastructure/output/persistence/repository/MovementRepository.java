package com.bank.account_service.infrastructure.output.persistence.repository;

import com.bank.account_service.infrastructure.output.persistence.entity.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<MovementEntity, Long> {

    List<MovementEntity> findByAccountId(Long accountId);

    List<MovementEntity> findByAccountIdAndDateBetween(Long accountId,
                                                       LocalDateTime startDate,
                                                       LocalDateTime endDate);
}