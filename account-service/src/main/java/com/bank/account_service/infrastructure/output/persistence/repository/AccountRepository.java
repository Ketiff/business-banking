package com.bank.account_service.infrastructure.output.persistence.repository;

import com.bank.account_service.infrastructure.output.persistence.entity.AccountEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AccountRepository extends JpaRepository<AccountEntity, Long> {

    Optional<AccountEntity> findByAccountNumber(String accountNumber);

    List<AccountEntity> findByCustomerId(Long customerId);

    boolean existsByAccountNumber(String accountNumber);
}