package com.capco.transaction.api;

import com.capco.transaction.domain.Transaction;
import com.capco.transaction.service.TransactionService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/transactions")
public class TransactionController {

    private final TransactionService transactionService;

    public TransactionController(TransactionService transactionService) {
        this.transactionService = transactionService;
    }

    @PostMapping
    public ResponseEntity<Transaction> submitTransaction(@Valid @RequestBody Transaction transaction) {
        Transaction saved = transactionService.submitTransaction(transaction);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    }

    @GetMapping
    public List<Transaction> getAllTransactions() {
        return transactionService.getAllTransactions();
    }

    @GetMapping("/{transactionId}")
    public ResponseEntity<Transaction> getTransaction(@PathVariable String transactionId) {
        return transactionService.getByTransactionId(transactionId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }
}
