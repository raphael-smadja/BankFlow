package com.bankflow.account.api;

import com.bankflow.account.application.TransferService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/transfers")
public class TransferController {

    private final TransferService transferService;

    public TransferController(
            TransferService transferService
    ) {
        this.transferService = transferService;
    }

    @PostMapping
    public ResponseEntity<Map<String, UUID>> transfer(
            @Valid @RequestBody TransferRequest request
    ) {
        UUID transactionId = transferService.transfer(
                request.sourceAccountId(),
                request.destinationAccountId(),
                request.amount()
        );

        return ResponseEntity.ok(
                Map.of(
                        "transactionId",
                        transactionId
                )
        );
    }
}