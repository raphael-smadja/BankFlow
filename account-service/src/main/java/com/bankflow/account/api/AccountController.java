package com.bankflow.account.api;

import com.bankflow.account.application.AccountService;
import com.bankflow.account.domain.Account;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/accounts")
public class AccountController {

    private final AccountService accountService;

    public AccountController(
            AccountService accountService
    ) {
        this.accountService = accountService;
    }

    @PostMapping
    public ResponseEntity<AccountResponse> create(
            @Valid @RequestBody CreateAccountRequest request
    ) {
        Account account =
                accountService.create(request);

        return ResponseEntity
                .created(
                        URI.create(
                                "/api/v1/accounts/"
                                        + account.getId()
                        )
                )
                .body(
                        AccountResponse.from(account)
                );
    }

    @GetMapping("/{id}")
    public AccountResponse findById(
            @PathVariable UUID id
    ) {
        return AccountResponse.from(
                accountService.findById(id)
        );
    }

    @PostMapping("/{id}/deposit")
    public AccountResponse deposit(
            @PathVariable UUID id,
            @Valid @RequestBody DepositRequest request
    ) {
        return AccountResponse.from(
                accountService.deposit(
                        id,
                        request.amount()
                )
        );
    }
}