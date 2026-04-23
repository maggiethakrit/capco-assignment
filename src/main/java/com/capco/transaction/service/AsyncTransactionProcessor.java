package com.capco.transaction.service;

import com.capco.transaction.domain.Transaction;
import com.capco.transaction.domain.TransactionStatus;
import com.capco.transaction.infrastructure.ExternalPaymentClient;
import com.capco.transaction.infrastructure.dto.PaymentRequest;
import com.capco.transaction.infrastructure.dto.PaymentResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.util.concurrent.CompletableFuture;

@Component
public class AsyncTransactionProcessor {

    private static final Logger log = LoggerFactory.getLogger(AsyncTransactionProcessor.class);
    private final ExternalPaymentClient paymentClient;
    private final TransactionService transactionService;

    public AsyncTransactionProcessor(ExternalPaymentClient paymentClient, TransactionService transactionService) {
        this.paymentClient = paymentClient;
        this.transactionService = transactionService;
    }

    @Retryable(retryFor = { RuntimeException.class }, maxAttempts = 3, backoff = @Backoff(delay = 2000, multiplier = 2))
    @Async("transactionTaskExecutor")
    public CompletableFuture<Void> processTransaction(Transaction tx) {
        log.debug("Starting async processing for transaction: {}", tx.getTransactionId());

        try {
            // Step 1 : Lock status PENDING -> PROCESSING protect schedule duplicate
            transactionService.updateStatus(tx.getId(), TransactionStatus.PROCESSING, null);

            PaymentRequest request = PaymentRequest.builder()
                    .transactionId(tx.getTransactionId())
                    .amount(tx.getAmount())
                    .currency(tx.getCurrency())
                    .payee(tx.getPayee())
                    .build();

            // Step 2 : Call external payment gateway
            PaymentResponse response = paymentClient.submitPayment(request);

            // Step 4 : Update Result
            if ("success".equalsIgnoreCase(response.getStatus())) {
                transactionService.updateStatus(tx.getId(), TransactionStatus.SUCCESS, null);
                log.info("Transaction {} processed successfully", tx.getTransactionId());
            } else {
                transactionService.updateStatus(tx.getId(), TransactionStatus.FAILED,
                        "External gateway returned failure status");
            }
        } catch (Exception e) {
            // Step 3 : Error Handling & Retry
            log.error("Failed to process transaction {}: {}", tx.getTransactionId(), e.getMessage());
            transactionService.updateStatus(tx.getId(), TransactionStatus.FAILED, e.getMessage());
            throw e; // Rethrow for @Retryable
        }

        return CompletableFuture.completedFuture(null);
    }

    @org.springframework.retry.annotation.Recover
    public void recover(RuntimeException e, Transaction tx) {
        log.error("Retry limit reached for transaction {}. Final failure: {}", tx.getTransactionId(), e.getMessage());
        transactionService.updateStatus(tx.getId(), TransactionStatus.FAILED, "Retry limit reached: " + e.getMessage());
    }
}
