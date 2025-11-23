package com.bank.account_service.infrastructure.output.persistence.repository;

import com.bank.account_service.infrastructure.output.persistence.entity.MovementEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface MovementRepository extends JpaRepository<MovementEntity, Long> {

    List<MovementEntity> findByAccountId(Long accountId);
    @Query("SELECT m FROM MovementEntity m WHERE m.account.id = :accountId " +
            "AND m.date BETWEEN :startDate AND :endDate " +
            "ORDER BY m.date ASC")
    List<MovementEntity> findByAccountIdAndDateBetween(
            @Param("accountId") Long accountId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate
    );
    List<MovementEntity> findByAccountIdOrderByDateDesc(Long accountId);
    long countByAccountId(Long accountId);
}