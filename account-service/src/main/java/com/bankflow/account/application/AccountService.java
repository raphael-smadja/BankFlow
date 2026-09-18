package com.bankflow.account.application;

import com.bankflow.account.api.CreateAccountRequest;
import com.bankflow.account.domain.Account;
import com.bankflow.account.domain.AccountNotFoundException;
import com.bankflow.account.domain.IbanAlreadyExistsException;
import com.bankflow.account.domain.LedgerEntry;
import com.bankflow.account.infrastructure.AccountRepository;
import com.bankflow.account.infrastructure.LedgerEntryRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Currency;
import java.util.UUID;

@Service
@Transactional(readOnly = true)
public class AccountService {

    private static final Logger log =
            LoggerFactory.getLogger(AccountService.class);

    private final AccountRepository accountRepository;

    private final LedgerEntryRepository ledgerEntryRepository;

    public AccountService(
            AccountRepository accountRepository, LedgerEntryRepository ledgerEntryRepository
    ) {
        this.accountRepository = accountRepository;
        this.ledgerEntryRepository = ledgerEntryRepository;
    }

    @Transactional
    public Account create(
            CreateAccountRequest request
    ) {
        if (accountRepository.existsByIban(request.iban())) {
            throw new IbanAlreadyExistsException(
                    request.iban()
            );
        }

        Currency currency =
                Currency.getInstance(request.currency());

        Account account = Account.create(
                request.customerId(),
                request.iban(),
                currency,
                request.initialBalance()
        );

        Account saved =
                accountRepository.save(account);

        log.info(
                "Account created accountId={} customerId={} currency={}",
                saved.getId(),
                saved.getCustomerId(),
                saved.getCurrency()
        );

        return saved;
    }

    public Account findById(UUID id) {
        return accountRepository.findById(id)
                .orElseThrow(() ->
                        new AccountNotFoundException(id)
                );
    }

    @Transactional
    public Account deposit(
            UUID accountId,
            BigDecimal amount
    ) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(() ->
                        new AccountNotFoundException(accountId)
                );

        account.deposit(amount);

        UUID transactionId = UUID.randomUUID();

        LedgerEntry entry = LedgerEntry.credit(
                account.getId(),
                transactionId,
                amount,
                account.getCurrency()
        );

        ledgerEntryRepository.save(entry);

        log.info(
                "Deposit completed accountId={} transactionId={} amount={}",
                accountId,
                transactionId,
                amount
        );

        return account;
    }
}