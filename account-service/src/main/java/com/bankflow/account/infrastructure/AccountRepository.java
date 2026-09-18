package com.bankflow.account.infrastructure;

import com.bankflow.account.domain.Account;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AccountRepository
        extends JpaRepository<Account, UUID> {

    Optional<Account> findByIban(String iban);

    List<Account> findAllByCustomerId(UUID customerId);

    boolean existsByIban(String iban);
}